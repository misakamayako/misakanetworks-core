package per.misaka.misakanetworkscore.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfig {

    @Bean(name = ["JSONMapper"])
    fun getObjectMapper():ObjectMapper{
        val objectMapper = ObjectMapper()
        objectMapper.registerModules(KotlinModule.Builder().build())
        return objectMapper
    }

    @Bean
    fun redisTemplate(
        connectionFactory: RedisConnectionFactory,
        @Qualifier("JSONMapper")
        jsonMapper:ObjectMapper
    ): RedisTemplate<String, Any> {
        val template = RedisTemplate<String, Any>()
        template.connectionFactory = connectionFactory
        val jacksonSerializer = Jackson2JsonRedisSerializer(jsonMapper,Any::class.java)
        // 配置 Key 和 Value 的序列化
        template.keySerializer = StringRedisSerializer()
        template.valueSerializer = jacksonSerializer
        template.hashKeySerializer = StringRedisSerializer()
        template.hashValueSerializer = jacksonSerializer

        return template
    }
}
