package per.misaka.misakanetworkscore.config

import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler
import org.springframework.security.web.util.matcher.AntPathRequestMatcher

@Configuration
@EnableWebSecurity
class MultiHttpSecurityConfig {

    @Bean
    fun internalResource(http: HttpSecurity): SecurityFilterChain {
        http {
            csrf { disable() }
            httpBasic { disable() }
            formLogin { disable() }
            logout {
                logoutRequestMatcher = AntPathRequestMatcher("/logoutHandler")
                logoutSuccessHandler =
                    LogoutSuccessHandler { request, response, authentication ->
                        response?.status = HttpServletResponse.SC_NO_CONTENT
                        response?.writer?.flush()
                    }
            }
            sessionManagement { SessionCreationPolicy.STATELESS }
            authorizeRequests {
                authorize(HttpMethod.GET, "/**", permitAll)
                authorize(HttpMethod.POST, "/logoutHandler", permitAll)
                authorize(HttpMethod.POST, "/login", permitAll)
                authorize("/internalApi/**", permitAll)//authenticated)//hasRole("USER")
            }
        }
        return http.build()
    }
}
