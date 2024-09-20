package per.misaka.misakanetworkscore.service.impl

import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import per.misaka.misakanetworkscore.dto.LoginUser
import per.misaka.misakanetworkscore.repository.UserRepository
import per.misaka.misakanetworkscore.service.SecurityService

@Service
class SecurityServiceImpl : SecurityService {
    @Autowired
    private lateinit var userRepository: UserRepository
    override suspend fun findUserDetailByName(name: String): UserDetails? {
        val user = userRepository.findByUsername(name).awaitSingleOrNull() ?: return null
        val userDetails = LoginUser(User(user.username, user.password, emptyList()), emptyList())
        return userDetails
    }
}