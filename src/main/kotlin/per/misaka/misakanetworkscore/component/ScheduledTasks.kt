package per.misaka.misakanetworkscore.component

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import per.misaka.misakanetworkscore.repository.ArticleRepository
import per.misaka.misakanetworkscore.service.RedisService
import reactor.core.publisher.Mono.`when`

@Component
class ScheduledTasks {
    @Autowired
    lateinit var articleRepository: ArticleRepository

    @Autowired
    lateinit var redisService: RedisService

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    fun saveViewsToDataBase() {
        logger.info("start collect views data and save to database")
        val keys = redisService.getKeys("article:views:articleId:*")
        if (keys.isNullOrEmpty()) {
            logger.info("no data for update")
            return
        }
        logger.info("start insert for {} articles", keys.size)
        try {
            runBlocking {
                withContext(Dispatchers.IO) {
                    val forUpdate = keys.map { key ->
                        val id = key.split(":").last()
                        val value = redisService.getEntity("article:views:articleId:$id", Int::class)
                        logger.info("article {} have {} views", id, value)
                        redisService.removeEntity(key)
                        articleRepository.increaseViews(id.toInt(), value ?: 0)
                    }
                    `when`(forUpdate).block()
                }
            }
            logger.info("solidification succeed，start to clean data")
        } catch (ex: Exception) {
            logger.error("update failed, restart at next 00:00, error is:", ex)
            throw ex
        }
    }
}