package per.misaka.misakanetworkscore.utils

import org.slf4j.LoggerFactory
import org.springframework.security.authorization.AuthorizationDecision
import org.springframework.security.authorization.AuthorizationManager
import org.springframework.security.core.Authentication
import org.springframework.security.web.access.intercept.RequestAuthorizationContext
import org.springframework.stereotype.Service
import per.misaka.misakanetworkscore.exception.unofficialError.MaliciousUserDetected
import per.misaka.misakanetworkscore.service.RedisService
import java.util.concurrent.TimeUnit
import java.util.function.Supplier
import kotlin.system.exitProcess

@Service
class CoreAuthorizationManager(private val redisService: RedisService) :
    AuthorizationManager<RequestAuthorizationContext?> {
    private val logger = LoggerFactory.getLogger(CoreAuthorizationManager::class.java)

    override fun check(
        authentication: Supplier<Authentication>,
        context: RequestAuthorizationContext?
    ): AuthorizationDecision {
        val token = context?.request?.cookies?.find { it.name == "m.m." }?.value
        if (token != null && redisService.getEntity(token, String::class) != null) {
            return AuthorizationDecision(true)
        }
        val ip = context?.request?.remoteAddr
        if (ip != null) {
            val count = (redisService.getEntity(ip, Int::class) ?: 0) + 1
            if (count > 50) {
                logger.error("too many attack from $ip, auto added to blackList")
            } else {
                logger.warn("unknown login request, from ip:$ip, $count times in 30 minutes")
            }
            redisService.saveEntity(count, ip, 30, TimeUnit.MINUTES)
            if (count > 30) {
                throw MaliciousUserDetected()
            }
            return AuthorizationDecision(false)
        } else {
            logger.error("can not local attacker's ip, shunt down server")//todo: send email to admin
            exitProcess(1)
        }
    }
}
