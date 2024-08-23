package per.misaka.misakanetworkscore.service

import com.vladsch.flexmark.ast.Image
import com.vladsch.flexmark.util.ast.NodeVisitor
import com.vladsch.flexmark.util.ast.VisitHandler
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import per.misaka.misakanetworkscore.component.FlexmarkComponent
import per.misaka.misakanetworkscore.constants.OSSBucket
import per.misaka.misakanetworkscore.dto.ArticleDTO
import per.misaka.misakanetworkscore.dto.PageResultDTO
import per.misaka.misakanetworkscore.dto.QueryResultArticleDTO
import per.misaka.misakanetworkscore.entity.*
import per.misaka.misakanetworkscore.exception.InternalServerException
import per.misaka.misakanetworkscore.exception.NotFoundException
import per.misaka.misakanetworkscore.repository.ArticleHistoryRepository
import per.misaka.misakanetworkscore.repository.ArticleRepository
import per.misaka.misakanetworkscore.repository.ArticleToCategoryRepository
import per.misaka.misakanetworkscore.repository.FileMappingOfArticleRepository
import per.misaka.misakanetworkscore.utils.ImageSourceReplacer
import per.misaka.misakanetworkscore.utils.gzipFile
import java.time.LocalDateTime
import java.util.*

@Service
class ArticleServiceImpl : ArticleService {

    @Autowired
    lateinit var articleRepository: ArticleRepository

    @Autowired
    lateinit var articleHistoryRepository: ArticleHistoryRepository

    @Autowired
    lateinit var fileMappingOfArticleRepository: FileMappingOfArticleRepository

    @Autowired
    lateinit var articleToCategoryRepository: ArticleToCategoryRepository

    @Autowired
    lateinit var flexmarkComponent: FlexmarkComponent

    @Autowired
    lateinit var localOSSService: OSSService

    val log: Logger = LogManager.getLogger(this::class.java)

    override suspend fun checkIfExists(id: Int): Boolean {
        return articleRepository.existsByIdAndHasDeleteFalse(id).awaitSingle()
    }

    override fun renderMarkdownToHtml(content: String, replaceOrigin: String?): Pair<String, HashMap<String, String>?> {
        var imageSourceReplacer: ImageSourceReplacer? = null
        val visitor: NodeVisitor? = if (replaceOrigin != null) {
            imageSourceReplacer =
                ImageSourceReplacer("${localOSSService.getBucketURL(OSSBucket.Article)}/$replaceOrigin")
            NodeVisitor(
                VisitHandler(Image::class.java, imageSourceReplacer)
            )
        } else {
            null
        }
        val html = flexmarkComponent.renderToHtml(content, visitor)
        return html to (imageSourceReplacer?.imgUrls)
    }

    override fun renderMarkdownToHtml(content: String): String {
        return this.renderMarkdownToHtml(content, null).first
    }

    @Transactional
    override suspend fun createArticle(articleDTO: ArticleDTO): ArticleDTO {
        val folder = UUID.randomUUID().toString()
        val (html, img) = renderMarkdownToHtml(articleDTO.content, folder)
        val articleEntity = ArticleEntity(
            title = articleDTO.title,
            folder = folder,
            brief = articleDTO.brief
        ).let(articleRepository::save).awaitSingle()
        val articleId = articleEntity.id
        if (articleId == null) {
            log.error("插入失败，原始值:{}", articleDTO)
            throw InternalServerException("插入失败")
        }
        ArticleHistory(article = articleId, version = 1)
            .let(articleHistoryRepository::save).awaitSingle()
        if (articleDTO.category.isNotEmpty()) {
            articleDTO.category
                .map {
                    ArticleToCategoryEntity(articleId, it)
                }
                .let(articleToCategoryRepository::saveAll)
                .then().awaitSingleOrNull()
        }
        if (!img.isNullOrEmpty()) {
            fileMappingOfArticleRepository.saveAll(img.map {
                FileMappingOfArticle(
                    OSSBucket.Article.value,
                    it.value,
                    articleId
                )
            }).then().awaitSingleOrNull()
        }
        var content = articleDTO.content
        val ossTask: MutableList<Deferred<*>> = img!!.map { (oldPath, newPath) ->
            content = content.replace(oldPath, newPath)
            localOSSService.copyObject(OSSBucket.Temp, oldPath, OSSBucket.Article, newPath)
        }.toMutableList()
        ossTask += listOf(
            localOSSService.putObject(
                OSSBucket.Article,
                "$folder/v1.md.gz",
                gzipFile(content.byteInputStream())
            ),
            localOSSService.putObject(
                OSSBucket.Article,
                "$folder/v1.fragment.gz",
                gzipFile(html.byteInputStream())
            )
        )
        ossTask.joinAll()
        return articleDTO.copy(id = articleId)
    }

    @Transactional
    override suspend fun updateArticle(articleDTO: ArticleDTO) {
        val mainId = articleDTO.id!!
        val oldDataArticleEntity =
            articleRepository.findById(mainId).awaitSingleOrNull() ?: throw NotFoundException()
        //删除就类型记录
        articleToCategoryRepository.deleteAllByArticle(mainId).awaitSingleOrNull()

        val oldHistory = articleHistoryRepository
            .findArticleHistoryByArticleAndStatusIs(mainId, ArticleStatus.published)
            .awaitSingleOrNull()
        if (oldHistory == null) {
            log.error("查询{}的历史失败", mainId)
            throw InternalServerException("查询${mainId}的历史失败")
        }
        //旧历史设置为存档
        oldHistory
            .copy(status = ArticleStatus.archived, updateAt = LocalDateTime.now())
            .let(articleHistoryRepository::save)
            .awaitSingle()
        //新记录
        val newRecord = oldHistory.copy(version = oldHistory.version + 1, id = null)
            .let(articleHistoryRepository::save)
            .awaitSingle()
        //保存新的分类
        if (articleDTO.category.isNotEmpty()) {
            articleDTO.category.map {
                ArticleToCategoryEntity(mainId, it)
            }
                .let(articleToCategoryRepository::saveAll)
                .then().awaitSingleOrNull()
        }
        val oldImg = fileMappingOfArticleRepository
            .findAllByConnectFileAndDeleteFlagIsFalse(mainId)
            .collectList()
            .awaitSingle()
        val needRemove = HashSet<String>()
        needRemove.addAll(oldImg.map { it.fileKey })
        val folder = oldDataArticleEntity.folder
        val (html, img) = renderMarkdownToHtml(articleDTO.content, folder)
        img as HashMap<String, String>
        var content = articleDTO.content
        //移除所有在用的图片，并且从Img中移除防止重复移动
        img.iterator().forEach { (oldPath, newPath) ->
            content = content.replace(oldPath, newPath)
            if (oldPath in needRemove) {
                needRemove.remove(oldPath)
                img.remove(oldPath)
            }
        }
        //删除不再使用的图片
        val count = needRemove
            .toList()
            .let(fileMappingOfArticleRepository::deleteAllByFileKeyIn)
            .awaitSingleOrNull()
        //保存新的图片关系
        if (img.isNotEmpty()) {
            img.map { (_, newPath) ->
                FileMappingOfArticle(
                    bucket = OSSBucket.Article.value,
                    fileKey = newPath,
                    connectFile = mainId,
                    deleteFlag = false,
                )
            }
                .let(fileMappingOfArticleRepository::saveAll)
                .then().awaitSingleOrNull()
        }
        if (count != needRemove.size) {
            log.error(
                "删除mainId:{},version:{}时，数量不正确,应删除{},实际删除{}",
                articleDTO.id,
                oldHistory.version,
                needRemove.size,
                count
            )
        }
        val ossTask: MutableList<Deferred<*>> = img.map { (oldPath, newPath) ->
            localOSSService.copyObject(OSSBucket.Temp, oldPath, OSSBucket.Article, newPath)
        }.toMutableList()
        ossTask += needRemove.map {
            localOSSService.deleteObject(OSSBucket.Article, it)
        }
        ossTask += listOf(
            localOSSService.putObject(
                OSSBucket.Article,
                "$folder/v${newRecord.version}.md.gz",
                gzipFile(content.byteInputStream())
            ),
            localOSSService.putObject(
                OSSBucket.Article,
                "$folder/v${newRecord.version}.fragment.gz",
                gzipFile(html.byteInputStream())
            )
        )
        ossTask.joinAll()
        return
    }

    override suspend fun deleteArticle(articleId: Int) {
        if (articleRepository.existsById(articleId).awaitSingle()) {
            articleRepository.logicDeleteAll(articleId).awaitSingle()
        } else {
            throw NotFoundException("no such a article")
        }
    }

    override suspend fun queryArticleList(page: Int, pageSize: Int): PageResultDTO<QueryResultArticleDTO?> {
        val recordTask = articleRepository.findAllArticle(pageSize, pageSize * (page - 1)).collectList()
        val totalTask = articleRepository.getCount()
        return PageResultDTO(
            list = recordTask.awaitSingleOrNull(),
            totalElements = totalTask.awaitSingle(),
            currentPage = page,
            currentPageSize = pageSize
        )
    }

    override suspend fun getArticleDetail(id: Int): QueryResultArticleDTO? {
        return articleRepository.getArticleDetail(id).awaitSingleOrNull()
    }

    override suspend fun getImagesOfArticle(articleID: Int): List<String> {
        return fileMappingOfArticleRepository
            .findAllByConnectFileAndDeleteFlagIsFalse(articleID)
            .map { "//${it.bucket}.oss-cn-shanghai.aliyuncs.com/${it.fileKey}" }
            .collectList()
            .awaitSingle()
    }
}
