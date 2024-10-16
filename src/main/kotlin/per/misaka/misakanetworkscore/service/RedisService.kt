package per.misaka.misakanetworkscore.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

@Service
class RedisService {
    @Autowired
    private lateinit var redisTemplate: RedisTemplate<String, Any>

    private val objectMapper = jacksonObjectMapper()
    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    // 存储任意类型的实体对象
    fun <T : Any> saveEntity(entity: T, key: String, timeout: Long? = null, unit: TimeUnit? = null) {
        val value = convertToString(entity)
        log.info("stored, key:{},value:{}",key,value)
        if (timeout != null && unit != null) {
            redisTemplate.opsForValue().set(key, value, timeout, unit)
        } else {
            redisTemplate.opsForValue().set(key, value)
        }
    }

    // 获取任意类型的实体对象
    fun <T : Any> getEntity(key: String, clazz: KClass<T>): T? {
        val value = redisTemplate.opsForValue().get(key) as? String
        return value?.let {
            try {
                objectMapper.readValue(it, clazz.java)
            } catch (e: Exception) {
                log.error("deserialize filed, message is : {}", e.message)
                null
            }
        }
    }

    // 删除指定键的数据
    fun removeEntity(key: String): Boolean {
        return redisTemplate.delete(key)
    }


    fun getKeys(keyPrefix: String): MutableSet<String>? {
        return redisTemplate.keys(keyPrefix)
    }

    // 通用转换方法 - 对象转字符串
    private fun <T : Any> convertToString(entity: T): String {
        return when (entity) {
            is String -> entity
            is Int, is Long, is Boolean, is Double, is Float -> entity.toString()
            else -> objectMapper.writeValueAsString(entity)
        }
    }
}

