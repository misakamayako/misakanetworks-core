package per.misaka.misakanetworkscore.service

import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import per.misaka.misakanetworkscore.constants.CookieFor
import java.util.UUID
import java.util.concurrent.TimeUnit

@Service
class TokenService {
    companion object {
        const val expiration_time: Long = 3600L
    }

    @Autowired
    private lateinit var redisTemplate: RedisTemplate<String, Any>

    private val log = LoggerFactory.getLogger(this::class.java)
    fun storeUserDetail(userId: Int): String {
        val token = nextToken()
        log.debug("store userDetails {} at {}", userId, token)
        redisTemplate.opsForValue().set(token, userId, expiration_time, TimeUnit.SECONDS)
        return token
    }

    private fun getId(token: String): Int? {
        return redisTemplate.opsForValue().get(token) as? Int
    }

    fun removeToken(token: String) {
        redisTemplate.delete(token)
    }

    private fun nextToken(): String {
        return UUID.randomUUID().toString()
    }

    fun getLoginUser(request: HttpServletRequest): Int? {
        val name = ("__Secure-".takeIf { request.isSecure } ?: "") + CookieFor.Token.toString()
        val token = request.cookies?.find { cookie -> cookie.name == name }
        if (token == null || token.value.isNullOrEmpty()) {
            log.info("no user detail for cookie: {}", token?.value)
            return null
        }
        return getId(token.value)
    }
}