package per.misaka.misakanetworkscore.service

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.supervisorScope
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import per.misaka.misakanetworkscore.dto.ArticleUploadDTO
import per.misaka.misakanetworkscore.entity.ArticleEntity
import per.misaka.misakanetworkscore.exception.BadRequestException
import per.misaka.misakanetworkscore.exception.InternalServerException
import per.misaka.misakanetworkscore.repository.ArticleRepository
import per.misaka.misakanetworkscore.utils.OSSInstance
import java.io.ByteArrayInputStream
import java.time.LocalDateTime

@Service
class ArticleService {
    @Autowired
    lateinit var articleRepository: ArticleRepository

    val log: Logger = LogManager.getLogger(this::class.java)


    suspend fun createArticle(articleUploadDTO: ArticleUploadDTO): ArticleEntity {
        if (!::articleRepository::isInitialized.get()) throw InternalServerException("数据库初始化失败")
        if (articleRepository.findByTitle(articleUploadDTO.title) != null) {
            throw BadRequestException(message = "已存在同名文件")
        }
        var insertResult: ArticleEntity? = null
        var ossJob: Deferred<Unit>? = null
        var sqlJob: Deferred<Unit>? = null
        val job = kotlin.runCatching {
            supervisorScope {
                ossJob = async {
                    OSSInstance.putObject(
                        "misaka-networks-article",
                        "${articleUploadDTO.title}.md",
                        ByteArrayInputStream(articleUploadDTO.content.toByteArray())
                    )
                }
                sqlJob = async {
                    insertResult = articleRepository.save(
                        ArticleEntity(
                            title = articleUploadDTO.title,
                            brief = articleUploadDTO.content.substring(0, 120),
                            views = 0,
                            createAt = LocalDateTime.now()
                        )
                    ).awaitSingle()
                }

                awaitAll(ossJob!!, sqlJob!!)
            }
        }
        if (job.isSuccess) {
            return insertResult ?: throw InternalError("插入错误")
        } else {
            if (ossJob?.isCompleted != true) {
                log.error("article service error:{}", "oss 上传失败")
            }
            if (sqlJob?.isCompleted != true) {
                log.error("article service error:{}", "数据库插入失败")
            }
            throw InternalError("插入错误")
        }

    }
}
