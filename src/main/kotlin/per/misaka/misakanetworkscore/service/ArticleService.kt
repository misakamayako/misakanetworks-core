package per.misaka.misakanetworkscore.service

import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import kotlinx.coroutines.reactor.awaitSingle
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import per.misaka.misakanetworkscore.constants.OSSBucket
import per.misaka.misakanetworkscore.dto.ArticleDTO
import per.misaka.misakanetworkscore.dto.ArticleUploadDTO
import per.misaka.misakanetworkscore.entity.ArticleEntity
import per.misaka.misakanetworkscore.entity.DeleteConfirmEntity
import per.misaka.misakanetworkscore.exception.BadRequestException
import per.misaka.misakanetworkscore.exception.InternalServerException
import per.misaka.misakanetworkscore.exception.NotFoundException
import per.misaka.misakanetworkscore.repository.ArticleRepository
import per.misaka.misakanetworkscore.repository.DeleteConfirmRepository
import per.misaka.misakanetworkscore.utils.OSSInstance
import java.util.*

@Service
class ArticleService {

    @Autowired
    lateinit var articleRepository: ArticleRepository

    @Autowired
    lateinit var deleteConfirmRepository: DeleteConfirmRepository
    val log: Logger = LogManager.getLogger(this::class.java)

    @Transactional
    suspend fun createArticle(articleUploadDTO: ArticleUploadDTO) {
        if (!::articleRepository::isInitialized.get()) throw InternalServerException("数据库初始化失败")
        if (articleRepository.findByTitle(articleUploadDTO.title) != null) {
            throw BadRequestException(message = "已存在同名文件")
        }
        val parser = Parser.builder().build()
        val render = HtmlRenderer.builder().build()
        val html = render.render(parser.parse(articleUploadDTO.content))
        val preview = html.substring(0..200)
        val markdownUrl = OSSInstance.putObject(
            OSSBucket.Article.value,
            "${articleUploadDTO.title}.md",
            articleUploadDTO.content.byteInputStream()
        )
        val htmlUrl = OSSInstance.putObject(
            OSSBucket.Article.value,
            "${articleUploadDTO.title}.fragment",
            html.byteInputStream()
        )
        val newArticleEntity = ArticleEntity(
            title = articleUploadDTO.title,
            markdownUrl = markdownUrl.response.uri,
            htmlUrl = htmlUrl.response.uri,
            preview = preview,
            author = "@misakamayako"
        )
        try {
            articleRepository.save(newArticleEntity).awaitSingle()
        } catch (e: Exception) {
            log.error(e.message)
            OSSInstance.deleteObject(OSSBucket.Article.value, markdownUrl.response.uri)
            OSSInstance.deleteObject(OSSBucket.Article.value, htmlUrl.response.uri)
            throw InternalServerException(e.message)
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
}
