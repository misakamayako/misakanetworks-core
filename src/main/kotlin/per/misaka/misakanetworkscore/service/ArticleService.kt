package per.misaka.misakanetworkscore.service

import com.aliyun.oss.model.PutObjectResult
import com.vladsch.flexmark.ast.Image
import com.vladsch.flexmark.util.ast.NodeVisitor
import com.vladsch.flexmark.util.ast.VisitHandler
import kotlinx.coroutines.*
import kotlinx.coroutines.reactor.awaitSingle
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import per.misaka.misakanetworkscore.component.FlexmarkComponent
import per.misaka.misakanetworkscore.constants.OSSBucket
import per.misaka.misakanetworkscore.dto.ArticleDTO
import per.misaka.misakanetworkscore.dto.ArticleUploadDTO
import per.misaka.misakanetworkscore.entity.ArticleEntity
import per.misaka.misakanetworkscore.entity.ArticleToCategoryEntity
import per.misaka.misakanetworkscore.entity.DeleteConfirmEntity
import per.misaka.misakanetworkscore.exception.BadRequestException
import per.misaka.misakanetworkscore.exception.InternalServerException
import per.misaka.misakanetworkscore.exception.NotFoundException
import per.misaka.misakanetworkscore.repository.ArticleRepository
import per.misaka.misakanetworkscore.repository.ArticleToCategoryRepository
import per.misaka.misakanetworkscore.repository.DeleteConfirmRepository
import per.misaka.misakanetworkscore.utils.ImageSourceReplacer
import java.time.LocalDateTime
import java.util.*

@Service
class ArticleService {

    @Autowired
    lateinit var articleRepository: ArticleRepository

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
        if (articleRepository.findByTitle(articleUploadDTO.title) != null) {
            throw BadRequestException(message = "已存在同名文件")
        }
        val newName = UUID.randomUUID().toString()
        if (articleRepository.findByTitle(newName) != null) {
            throw BadRequestException(message = "未知错误")
        }
        val imageSourceReplacer = ImageSourceReplacer("${localOSSService.getBucketURL(OSSBucket.Article)}/$newName")
        val visitor = NodeVisitor(
            VisitHandler(Image::class.java, imageSourceReplacer)
        )
        val html = flexmarkComponent.renderToHtml(articleUploadDTO.content, visitor)

        val brief = articleUploadDTO.brief
        withContext(Dispatchers.IO) {
            val copyJobs = imageSourceReplacer.imgUrls.map { (old,new) ->
                async {
                    localOSSService.copyObject(OSSBucket.Temp.value, old, OSSBucket.Article.value, "$newName/$new")
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
            )
            try {
                val (id) = articleRepository.save(newArticleEntity).awaitSingle()
                log.info("new Article as id: $id")
                if (id != null) {
                    articleUploadDTO.categories.forEach { category ->
                        articleToCategoryRepository.save(ArticleToCategoryEntity(article = id, category = category))
                    }
                }
            } catch (e: Exception) {
                log.error("保存文件失败",e)
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

    suspend fun sendDeleteConfirm(id: Int) {
        if (articleRepository.existsById(id).awaitSingle()) {
            val uuid = UUID.randomUUID()
            deleteConfirmRepository.save(DeleteConfirmEntity(uuid = uuid.toString(), type = "article"))
        } else {
            throw NotFoundException("找不到对应的文章")
        }
    }

    suspend fun preview(content: String): String = flexmarkComponent.renderToHtml(content)

    suspend fun getArticleList(page:Int,pageSize:Int){
        articleRepository.queryByPage(page,pageSize*page).map {tuple->
            val id = tuple.get("id", Long::class.java)
            val title = tuple.get("title", String::class.java)
            val markdownUrl = tuple.get("markdown_url", String::class.java)
            val htmlUrl = tuple.get("html_url", String::class.java)
            val brief = tuple.get("brief", String::class.java)
            val author = tuple.get("author", String::class.java)
            val createdAt = tuple.get("created_at", LocalDateTime::class.java)
            val updatedAt = tuple.get("updated_at", LocalDateTime::class.java)
            val categoriesString = tuple.get("categories", String::class.java)
            val categoryTypesString = tuple.get("category_types", String::class.java)

            val categories = categoriesString?.split(", ") ?: emptyList<String>()
            val categoryTypes = categoryTypesString?.split(", ") ?: emptyList<String>()
        }
    }
}
