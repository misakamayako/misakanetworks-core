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

    private val logger:Logger = LoggerFactory.getLogger(this::class.java)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val user = tokenService.getLoginUser(request)
        logger.info("attempt user:{}",user)
        if (user != null && SecurityContextHolder.getContext().authentication == null) {
            val token = CustomerUsernamePasswordAuthenticationToken.authenticated(
                user, user.password,
                user.authorities as Collection<GrantedAuthority>?
            )
            token.details = WebAuthenticationDetailsSource().buildDetails((request))
            SecurityContextHolder.getContext().authentication = token
        }
        filterChain.doFilter(request, response)
    }
}