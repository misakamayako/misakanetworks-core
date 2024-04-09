package per.misaka.misakanetworkscore.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

@Service
class RedisService(private val redisTemplate: StringRedisTemplate) {
    private val objectMapper = jacksonObjectMapper()

    fun <T : Any> saveEntity(entity: T, key: String, timeout: Long, unit: TimeUnit) {
        val json = objectMapper.writeValueAsString(entity)
        redisTemplate.opsForValue().set(key, json, timeout, unit)
    }

    fun <T : Any> getEntity(key: String, clazz: KClass<T>): T? {
        val json = redisTemplate.opsForValue().get(key)
        return json?.let {
            objectMapper.readValue(it, clazz.java)
        }
    }
    fun removeEntity(key:String):Boolean{
        return redisTemplate.delete(key)
    }

    fun check(s:String):Boolean{
        return redisTemplate.hasKey(s)
    }
}
