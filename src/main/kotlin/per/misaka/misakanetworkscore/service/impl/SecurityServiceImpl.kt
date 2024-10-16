package per.misaka.misakanetworkscore.service.impl

import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import per.misaka.misakanetworkscore.repository.UserRepository
import per.misaka.misakanetworkscore.service.SecurityService
import per.misaka.misakanetworkscore.service.UserDetailInMine

@Service
class SecurityServiceImpl : SecurityService {
    @Autowired
    private lateinit var userRepository: UserRepository
    override suspend fun findUserDetailByName(name: String): UserDetailInMine? {
        val user = userRepository.findByUsername(name).awaitSingleOrNull() ?: return null
        val userDetails :UserDetailInMine=TODO("UserDetailInMine()")
        return userDetails
    }
}