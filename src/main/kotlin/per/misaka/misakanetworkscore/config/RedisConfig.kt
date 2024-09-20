package per.misaka.misakanetworkscore.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfig {

    @Bean
    fun redisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<String, Any> {
        val template = RedisTemplate<String, Any>()
        template.connectionFactory = connectionFactory

        val objectMapper = ObjectMapper()
        // 使用 JSON 序列化
        val jacksonSerializer = Jackson2JsonRedisSerializer(objectMapper,Any::class.java)

        // 配置 Key 和 Value 的序列化
        template.keySerializer = StringRedisSerializer()
        template.valueSerializer = jacksonSerializer
        template.hashKeySerializer = StringRedisSerializer()
        template.hashValueSerializer = jacksonSerializer

        return template
    }
}
