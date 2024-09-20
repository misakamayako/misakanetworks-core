package per.misaka.misakanetworkscore.component

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component
import per.misaka.misakanetworkscore.constants.CookieFor
import per.misaka.misakanetworkscore.dto.LoginUser
import per.misaka.misakanetworkscore.service.TokenService

@Component
class CustomerAuthenticationSuccessHandler : AuthenticationSuccessHandler {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @Autowired
    private lateinit var tokenService: TokenService
    override fun onAuthenticationSuccess(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        authentication: Authentication?
    ) {
        if (request == null || response == null) return
        val user = authentication?.principal as? LoginUser ?: return
        val token = tokenService.storeUserDetail(user, 3600)
        val name = ("__Secure-".takeIf { request.isSecure } ?: "") + CookieFor.Token.toString()
        val cookie = Cookie(name, token)
        with(cookie) {
            maxAge = 3600
            isHttpOnly = true
            secure = true
            path = "/"
        }
        response.addCookie(cookie)
        logger.debug("new login as {}, token is {}", user.username, token)
    }
}