package per.misaka.misakanetworkscore.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

@Service
class RedisService {
    @Autowired
    private lateinit var redisTemplate: StringRedisTemplate

    private val objectMapper = jacksonObjectMapper()

    // 存储任意类型的实体对象
    fun <T : Any> saveEntity(entity: T, key: String, timeout: Long? = null, unit: TimeUnit? = null) {
        val value = convertToString(entity)
        if (timeout != null && unit != null) {
            redisTemplate.opsForValue().set(key, value, timeout, unit)
        } else {
            redisTemplate.opsForValue().set(key, value)
        }
    }

    // 获取任意类型的实体对象
    fun <T : Any> getEntity(key: String, clazz: KClass<T>): T? {
        val value = redisTemplate.opsForValue().get(key)
        return value?.let { convertFromString(it, clazz) }
    }

    // 删除指定键的数据
    fun removeEntity(key: String): Boolean {
        return redisTemplate.delete(key)
    }

    // 检查键是否存在
    fun hasKey(key: String): Boolean {
        return redisTemplate.hasKey(key) ?: false
    }

    // 存储列表
    fun <T : Any> saveList(key: String, list: List<T>) {
        val json = objectMapper.writeValueAsString(list)
        redisTemplate.opsForList().rightPush(key, json)
    }

    // 获取列表
    fun <T : Any> getList(key: String, clazz: KClass<T>): List<T>? {
        val json = redisTemplate.opsForList().range(key, 0, -1)?.firstOrNull()
        return json?.let {
            objectMapper.readValue(it, objectMapper.typeFactory.constructCollectionType(List::class.java, clazz.java))
        }
    }

    // 存储集合
    fun <T : Any> saveSet(key: String, set: Set<T>) {
        val json = objectMapper.writeValueAsString(set)
        redisTemplate.opsForSet().add(key, json)
    }

    // 获取集合
    fun <T : Any> getSet(key: String, clazz: KClass<T>): Set<T>? {
        val json = redisTemplate.opsForSet().members(key)?.firstOrNull()
        return json?.let {
            objectMapper.readValue(it, objectMapper.typeFactory.constructCollectionType(Set::class.java, clazz.java))
        }
    }

    // 存储哈希
    fun <T : Any> saveHash(key: String, hashKey: String, entity: T) {
        val json = objectMapper.writeValueAsString(entity)
        redisTemplate.opsForHash<String, String>().put(key, hashKey, json)
    }

    // 获取哈希
    fun <T : Any> getHash(key: String, hashKey: String, clazz: KClass<T>): T? {
        val json = redisTemplate.opsForHash<String, String>().get(key, hashKey)
        return json?.let {
            objectMapper.readValue(it, clazz.java)
        }
    }

    fun getKeys(keyPrefix:String):MutableSet<String>?{
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

    // 通用转换方法 - 字符串转对象
    private fun <T : Any> convertFromString(value: String, clazz: KClass<T>): T? {
        return when (clazz) {
            String::class -> value as? T
            Int::class -> value.toIntOrNull() as? T
            Long::class -> value.toLongOrNull() as? T
            Boolean::class -> value.toBoolean() as T
            Double::class -> value.toDoubleOrNull() as? T
            Float::class -> value.toFloatOrNull() as? T
            else -> objectMapper.readValue(value, clazz.java)
        }
    }
}

