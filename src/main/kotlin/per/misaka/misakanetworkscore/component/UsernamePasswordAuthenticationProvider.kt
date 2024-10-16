package per.misaka.misakanetworkscore.component

import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import per.misaka.misakanetworkscore.dto.CustomerUsernamePasswordAuthenticationToken
import per.misaka.misakanetworkscore.exception.UnauthorizedException
import per.misaka.misakanetworkscore.service.SecurityService

@Component
class UsernamePasswordAuthenticationProvider : AuthenticationProvider {
    @Autowired
    private lateinit var securityService: SecurityService

    @Autowired
    @Qualifier("customerDelegatingPasswordEncoder")
    private lateinit var customerDelegatingPasswordEncoder: PasswordEncoder

    @Throws(UnauthorizedException::class)
    override fun authenticate(authentication: Authentication?): Authentication? {
        val token = (authentication as? CustomerUsernamePasswordAuthenticationToken) ?: return null
        if (token.name.isNullOrEmpty() || token.credentials.isNullOrEmpty()) throw UnauthorizedException("用户名或密码为空")
        val userDetails = runBlocking { securityService.findUserDetailByName(token.name) }
        if (userDetails == null || !customerDelegatingPasswordEncoder.matches(token.credentials!!, userDetails.password)) {
            throw UnauthorizedException("用户名或密码错误")
        }
        val authenticationToken =
            CustomerUsernamePasswordAuthenticationToken(userDetails.id, token.credentials, userDetails.authorities);
        authenticationToken.details = token.details
        return authenticationToken
    }

    override fun supports(authentication: Class<*>): Boolean {
        return CustomerUsernamePasswordAuthenticationToken::class.java.isAssignableFrom(authentication)
    }
}