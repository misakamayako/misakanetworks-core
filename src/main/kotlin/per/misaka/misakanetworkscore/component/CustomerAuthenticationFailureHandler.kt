package per.misaka.misakanetworkscore.component

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class CustomerAuthenticationFailureHandler : AuthenticationFailureHandler {

    private val logger = LoggerFactory.getLogger(this::class.java)
    private val failedAttempts = ConcurrentHashMap<String, Int>()
    private val attemptLimit = 5  // 最大失败次数

    override fun onAuthenticationFailure(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException
    ) {
        val clientIp = getClientIp(request)
        val attempts = failedAttempts.merge(clientIp, 1, Int::plus) ?: 1
        if (attempts > attemptLimit) {
            // 超过失败次数，返回444状态码，并记录日志
            response.status = 444
            response.writer.write("access blocked")
            logger.info(
                "IP: {} has exceeded the allowed number of authentication failures ({} attempts).",
                clientIp,
                attempts
            )
            return
        }
        logger.info("Authentication failed for IP: {}. Attempt count: {}", clientIp, attempts)
        // 默认返回 401 错误或其他处理
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.writer.write("Authentication failed")
    }

    private fun getClientIp(request: HttpServletRequest): String {
        val forwardedFor = request.getHeader("X-Forwarded-For")
        return if (forwardedFor != null && forwardedFor.isNotEmpty()) {
            forwardedFor.split(",")[0]
        } else {
            request.remoteAddr
        }
    }
}