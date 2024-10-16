package per.misaka.misakanetworkscore.filter

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.User
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import per.misaka.misakanetworkscore.constants.CookieFor
import per.misaka.misakanetworkscore.dto.CustomerUsernamePasswordAuthenticationToken
import per.misaka.misakanetworkscore.dto.LoginUser
import per.misaka.misakanetworkscore.dto.UserDTO
import per.misaka.misakanetworkscore.exception.BadRequestException
import per.misaka.misakanetworkscore.exception.MethodNotAllowException
import per.misaka.misakanetworkscore.service.TokenService

class CustomerUsernamePasswordAuthenticationFilter :
    AbstractAuthenticationProcessingFilter(DEFAULT_ANT_PATH_REQUEST_MATCHER) {
    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    @Autowired
    private lateinit var tokenService: TokenService

    companion object {
        @JvmStatic
        private val DEFAULT_ANT_PATH_REQUEST_MATCHER = AntPathRequestMatcher("/login", "POST")
    }

    private fun getUserName(request: HttpServletRequest): UserDTO? {
        val reader = request.reader
        val sb = StringBuilder()
        reader.use { bufferedReader ->
            bufferedReader.readLine()?.let {
                sb.append(it)
            }
        }
        val objectMapper = jacksonObjectMapper()
        return try {
            objectMapper.readValue<UserDTO>(sb.toString())
        } catch (e: Exception) {
            log.error("can not convert to UserDTO for error: {} for value:{}", e, sb.toString())
            null
        }
    }

    override fun attemptAuthentication(
        request: HttpServletRequest?,
        response: HttpServletResponse?
    ): Authentication? {
        if (request == null) return null
        if (request.method.uppercase() != "POST") {
            throw MethodNotAllowException("登陆方法不受支持")
        }
        val userDTO = getUserName(request) ?: throw BadRequestException("请求体错误")
        val loginUser = LoginUser(
            User(
                userDTO.username,
                userDTO.password,
                emptyList()
            ), emptyList()
        )
        val token = CustomerUsernamePasswordAuthenticationToken.unauthenticated(123, userDTO.password)
        this.authenticationManager.authenticate(token)
        val cookieName = ("__Secure-".takeIf { request.isSecure } ?: "") + CookieFor.Token.toString()
        val cookie = Cookie(cookieName, tokenService.storeUserDetail(token.principal!!))
        with(cookie) {
            path = "/"
            maxAge = TokenService.expiration_time.toInt()
            isHttpOnly = true
        }
        response?.addCookie(cookie)
        response?.status = HttpServletResponse.SC_NO_CONTENT
        return null
    }
}