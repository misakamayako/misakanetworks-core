package per.misaka.misakanetworkscore.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import per.misaka.misakanetworkscore.dto.CustomerUsernamePasswordAuthenticationToken
import per.misaka.misakanetworkscore.service.TokenService

@Component
class CustomerJWTAuthenticationFilter : OncePerRequestFilter() {

    @Autowired
    private lateinit var tokenService: TokenService

    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val user = tokenService.getLoginUser(request)
        if (user != null) {
            log.info("attempt user:{}", user)
        }
        if (user != null && SecurityContextHolder.getContext().authentication == null) {
            val token = CustomerUsernamePasswordAuthenticationToken.authenticated(
                user, "",
                emptyList()//TODO get from database
            )
            token.details = WebAuthenticationDetailsSource().buildDetails((request))
            SecurityContextHolder.getContext().authentication = token
        } else {
            log.debug("unAuthentication or login request with cookie {}", request.cookies)
        }
        filterChain.doFilter(request, response)
    }
}