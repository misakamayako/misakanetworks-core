package per.misaka.misakanetworkscore.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import per.misaka.misakanetworkscore.component.UsernamePasswordAuthenticationProvider
import per.misaka.misakanetworkscore.filter.CustomerJWTAuthenticationFilter
import per.misaka.misakanetworkscore.filter.CustomerUsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig {
    @Autowired
    private lateinit var authenticationManagerBuilder: AuthenticationManagerBuilder

    @Autowired
    private lateinit var usernamePasswordAuthenticationProvider: UsernamePasswordAuthenticationProvider

    @Autowired
    private lateinit var authenticationSuccessHandler: AuthenticationSuccessHandler

    @Autowired
    private lateinit var authenticationFailureHandler: AuthenticationFailureHandler

    @Bean
    fun bCryptPasswordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun authenticationManager(
        authenticationConfiguration: AuthenticationConfiguration,
        bCryptPasswordEncoder: BCryptPasswordEncoder
    ): AuthenticationManager {
        usernamePasswordAuthenticationProvider.setBCryptPasswordEncoder(bCryptPasswordEncoder)
        authenticationManagerBuilder.authenticationProvider(usernamePasswordAuthenticationProvider)
        return authenticationConfiguration.authenticationManager
    }

    @Bean
    fun usernamePasswordAuthenticationFilter(authenticationManager: AuthenticationManager): CustomerUsernamePasswordAuthenticationFilter {
        val filter = CustomerUsernamePasswordAuthenticationFilter()
        filter.setAuthenticationManager(authenticationManager)
        filter.setAuthenticationSuccessHandler(authenticationSuccessHandler)
        filter.setAuthenticationFailureHandler(authenticationFailureHandler)
        return filter
    }

    @Bean
    fun securityFilterChain(
        httpSecurity: HttpSecurity,
        customerUsernamePasswordAuthenticationFilter: CustomerUsernamePasswordAuthenticationFilter,
        customerJWTAuthenticationFilter: CustomerJWTAuthenticationFilter,
        authenticationManager: AuthenticationManager
    ): SecurityFilterChain {
        with(httpSecurity) {
            authorizeHttpRequests {
                it.requestMatchers(AntPathRequestMatcher("/**", "GET")).permitAll()
                it.requestMatchers(AntPathRequestMatcher("/login", "POST")).permitAll()
            }
            addFilterBefore(
                customerJWTAuthenticationFilter,
                UsernamePasswordAuthenticationFilter::class.java
            )
            addFilterBefore(
                customerUsernamePasswordAuthenticationFilter,
                UsernamePasswordAuthenticationFilter::class.java
            )
            authenticationManager(authenticationManager)
            formLogin { it.disable() }
            httpBasic { it.disable() }
            csrf { it.disable() }
            cors { it.disable() }
        }
        return httpSecurity.build()
    }
}