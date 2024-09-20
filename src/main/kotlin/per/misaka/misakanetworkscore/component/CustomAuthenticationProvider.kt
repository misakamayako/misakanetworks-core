package per.misaka.misakanetworkscore.component

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component
import per.misaka.misakanetworkscore.exception.AuthoritiesException

@Component
class CustomAuthenticationProvider : AuthenticationProvider  {
    @Autowired
    private lateinit var userDetailsService: UserDetailsService

    override fun authenticate(authentication: Authentication): Authentication? {
        val username = authentication.name
        val password = authentication.credentials.toString()

        // 使用 UserDetailsService 加载用户
        val userDetails = userDetailsService.loadUserByUsername(username)

        // 自定义密码校验
        if (password == userDetails.password) {
            return UsernamePasswordAuthenticationToken(userDetails, password, userDetails.authorities)
        }
        throw AuthoritiesException("Invalid credentials")
    }


    override fun supports(authentication: Class<*>): Boolean {
        return UsernamePasswordAuthenticationToken::class.java.isAssignableFrom(authentication)
    }
}
