package per.misaka.misakanetworkscore.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain


@Configuration
@EnableWebSecurity
class MultiHttpSecurityConfig {

    @Bean
    fun internalResource(http: HttpSecurity): SecurityFilterChain {
        http {
            csrf { disable() }
            httpBasic { disable() }
            formLogin { disable() }
            sessionManagement { SessionCreationPolicy.STATELESS }
            authorizeRequests {
                authorize(HttpMethod.GET, "/**", permitAll)
                authorize("/internalApi/**", authenticated)//hasRole("USER")
            }
        }
        return http.build()
    }
}
