package per.misaka.misakanetworkscore.service

import kotlinx.coroutines.runBlocking
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import per.misaka.misakanetworkscore.repository.UserRepository

@Service
class CoreUserDetailsService(private val userRepository: UserRepository) : UserDetailsService {
    override fun loadUserByUsername(username: String?): UserDetails? {
        if (username.isNullOrBlank()) {
            throw UsernameNotFoundException("$username not found")
        }
        val check = runBlocking { userRepository.findByUsername(username) }?:return null
        return User(check.username, check.password, listOf(SimpleGrantedAuthority("admin")))
    }
}
