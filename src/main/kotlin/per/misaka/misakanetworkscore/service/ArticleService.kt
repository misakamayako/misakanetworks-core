package per.misaka.misakanetworkscore.service

import com.aliyun.oss.model.PutObjectResult
import com.vladsch.flexmark.ast.Image
import com.vladsch.flexmark.util.ast.NodeVisitor
import com.vladsch.flexmark.util.ast.VisitHandler
import kotlinx.coroutines.*
import kotlinx.coroutines.reactive.awaitLast
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import per.misaka.misakanetworkscore.component.FlexmarkComponent
import per.misaka.misakanetworkscore.constants.OSSBucket
import per.misaka.misakanetworkscore.dto.*
import per.misaka.misakanetworkscore.entity.ArticleEntity
import per.misaka.misakanetworkscore.entity.ArticleToCategoryEntity
import per.misaka.misakanetworkscore.entity.FileMappingOfArticle
import per.misaka.misakanetworkscore.exception.BadRequestException
import per.misaka.misakanetworkscore.exception.InternalServerException
import per.misaka.misakanetworkscore.exception.NotFoundException
import per.misaka.misakanetworkscore.repository.ArticleRepository
import per.misaka.misakanetworkscore.repository.ArticleToCategoryRepository
import per.misaka.misakanetworkscore.repository.DeleteConfirmRepository
import per.misaka.misakanetworkscore.repository.FileMappingOfArticleRepository
import per.misaka.misakanetworkscore.utils.ImageSourceReplacer
import java.util.*

@Service
class ArticleService {

    @Autowired
    lateinit var articleRepository: ArticleRepository

    @Autowired
    lateinit var fileMappingOfArticleRepository: FileMappingOfArticleRepository

    @Autowired
    lateinit var articleToCategoryRepository: ArticleToCategoryRepository

    @Autowired
    lateinit var deleteConfirmRepository: DeleteConfirmRepository

    @Autowired
    lateinit var flexmarkComponent: FlexmarkComponent

    @Autowired
    lateinit var localOSSService: OSSService

    val log: Logger = LogManager.getLogger(this::class.java)

    @Transactional
    suspend fun createArticle(articleUploadDTO: ArticleUploadDTO) {
        if (articleRepository.findByTitle(articleUploadDTO.title).awaitSingleOrNull() != null) {
            throw BadRequestException(message = "已存在同名文件")
        }
        log.debug("--------------")
        val newName = UUID.randomUUID().toString()
        if (articleRepository.findByTitle(newName).awaitSingleOrNull() != null) {
            throw BadRequestException(message = "未知错误")
        }
        val imageSourceReplacer = ImageSourceReplacer("${localOSSService.getBucketURL(OSSBucket.Article)}/$newName")
        val visitor = NodeVisitor(
            VisitHandler(Image::class.java, imageSourceReplacer)
        )
        val html = flexmarkComponent.renderToHtml(articleUploadDTO.content, visitor)

        val brief = articleUploadDTO.brief
        withContext(Dispatchers.IO) {
            val imgList = ArrayList<FileMappingOfArticle>()
            val copyJobs = imageSourceReplacer.imgUrls.map { (old, new) ->
                async {
                    localOSSService.copyObject(OSSBucket.Temp.value, old, OSSBucket.Article.value, "$newName/$new")
                    imgList.add(FileMappingOfArticle(OSSBucket.Article.value, "$newName/$new"))
                }
            }
            val markdownUrlDeferred: Deferred<PutObjectResult>
            val htmlUrlDeferred: Deferred<PutObjectResult>
            coroutineScope {
                markdownUrlDeferred = localOSSService.putObject(
                    OSSBucket.Article.value,
                    "$newName.md",
                    articleUploadDTO.content.byteInputStream()
                )
                htmlUrlDeferred = localOSSService.putObject(
                    OSSBucket.Article.value,
                    "$newName.fragment",
                    html.byteInputStream()
                )
            }
            markdownUrlDeferred.await()
            htmlUrlDeferred.await()
            copyJobs.awaitAll()
            val newArticleEntity = ArticleEntity(
                title = articleUploadDTO.title,
                markdownUrl = "$newName.md",
                htmlUrl = "$newName.fragment",
                brief = brief,
                author = "@misakamayako",
                views = 0
            )
            try {
                val id = articleRepository.save(newArticleEntity).awaitSingle().id
                log.info("new Article as id: $id")
                if (id != null) {
                    imgList.forEach { it.id = id.toLong() }
                    awaitAll(
                        articleUploadDTO.categories.map { category ->
                            (ArticleToCategoryEntity(article = id, category = category))
                        }.let { async { articleToCategoryRepository.saveAll(it).awaitLast() } },
                        async { if (imgList.isNotEmpty()) fileMappingOfArticleRepository.saveAll(imgList).awaitLast() }
                    )
                }
            } catch (e: Exception) {
                log.error("保存文件失败", e)
                localOSSService.deleteObject(OSSBucket.Article.value, "$newName.md")
                localOSSService.deleteObject(OSSBucket.Article.value, "$newName.fragment")
                throw InternalServerException(e.message)
            }
        }
    }

    @Transactional(rollbackFor = [InternalServerException::class])
    suspend fun updateArticle(id: Int, articleDTO: ArticleDTO) {
        log.info("updating article $id")

    }


    suspend fun preview(content: String): String = flexmarkComponent.renderToHtml(content)

    suspend fun getArticleList(page: Int, pageSize: Int): PageResultDTO<QueryResultArticleDTO> {
        return articleRepository.queryByPage(pageSize, pageSize * (page - 1))
            .collectList().zipWith(articleRepository.count()).map {
                val list = it.t1
                val totalCount = it.t2
                val totalPage = (totalCount + pageSize - 1) / pageSize
                PageResultDTO(
                    list,
                    totalPage,
                    totalCount,
                    page,
                    pageSize
                )
            }.awaitSingle()

    }

    suspend fun getArticleDetail(id: Long): ArticleDetailDTO {
        return withContext(Dispatchers.IO) {
            val article = articleRepository.getArticleDetailById(id).awaitSingle() ?: throw NotFoundException()
            val files =
                fileMappingOfArticleRepository.findAllByConnectFile(id).collectList().awaitSingle().map { it.fileKey }
            val content = localOSSService.getObject(OSSBucket.Article, "${article.title}.md").await()
            ArticleDetailDTO(
                id = article.id,
                title = article.title,
                brief = article.brief,
                content = String(content.response.content.readAllBytes()),
                categories = article.categories?.split(",")?.map { it.toInt() } ?: emptyList(),
                imgList = files
            )
        }
    }

    suspend fun deleteArticle(id: Long) {
        if (articleRepository.existsById(id).awaitSingle()) {
            withContext(Dispatchers.IO) {
                articleRepository.deleteArticleById(id).block()
            }
        } else {
            throw NotFoundException("no such a article")
        }
    }
}
