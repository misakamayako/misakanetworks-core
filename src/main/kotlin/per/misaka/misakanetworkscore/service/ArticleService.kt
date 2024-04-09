package per.misaka.misakanetworkscore.service

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.withTimeoutOrNull
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import per.misaka.misakanetworkscore.dto.ArticleUploadDTO
import per.misaka.misakanetworkscore.entity.ArticleEntity
import per.misaka.misakanetworkscore.exception.BadRequestException
import per.misaka.misakanetworkscore.repository.ArticleRepository
import per.misaka.misakanetworkscore.utils.OSSInstance
import java.io.ByteArrayInputStream
import java.time.LocalDateTime

@Service
class ArticleService {
    @Autowired
    lateinit var db: ArticleRepository

    companion object {
        @JvmStatic
        val log: Logger = LogManager.getLogger(this::class.java)
    }


    suspend fun createArticle(articleUploadDTO: ArticleUploadDTO) = coroutineScope {
        if (db.findByTitle(articleUploadDTO.title) != null) {
            throw BadRequestException(message = "已存在同名文件")
        }
        var insertResult: ArticleEntity? = null
        val ossJob = launch {
            OSSInstance.putObject(
                "misaka-networks-article",
                "${articleUploadDTO.title}.md",
                ByteArrayInputStream(articleUploadDTO.content.toByteArray())
            )
        }
        val sqlJob = launch {
            insertResult = db.save(
                ArticleEntity(
                    title = articleUploadDTO.title,
                    brief = articleUploadDTO.content.substring(0, 120),
                    views = 0,
                    createAt = LocalDateTime.now()
                )
            ).awaitSingle()
        }
        val execute = withTimeoutOrNull(5000) {
            ossJob.start()
            sqlJob.start()
            "Done"
        }
        if (execute !== "Done") {
            if (!ossJob.isCompleted) {
                log.error("article service error:{}", "oss 上传失败")
            }
            if (!sqlJob.isCompleted) {
                log.error("article service error:{}", "数据库插入失败")
            }
        }
        return@coroutineScope insertResult ?: throw InternalError("插入错误")
    }
}
