package per.misaka.misakanetworkscore.component

import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.provisioning.UserDetailsManager
import org.springframework.stereotype.Component
import per.misaka.misakanetworkscore.entity.UserEntity
import per.misaka.misakanetworkscore.repository.UserRepository
import per.misaka.misakanetworkscore.service.CorePasswordEncoder

@Component
class CoreUserDetailsManager(private val userDb: UserRepository, private val encoder: CorePasswordEncoder) :
    UserDetailsManager {
    companion object {
        @JvmStatic
        private val logger = LoggerFactory.getLogger(CorePasswordEncoder::class.java)
    }

    override fun loadUserByUsername(username: String?): UserDetails? {
        if (username.isNullOrEmpty()) return null
        logger.info("new login: username:$username")
        val user = runBlocking { userDb.findByUsername(username).awaitSingle()  }?: return null
        return User(user.username, user.password, emptyList())
    }

    override fun createUser(user: UserDetails?) {
        if (user == null) return
        val coded = encoder.encode(user.password)
        runBlocking {
            userDb.save(UserEntity(username = user.username, password = coded, enabled = true))
        }
    }

    override fun updateUser(user: UserDetails?) {
        if (user == null) return
        userDb.save(
            UserEntity(
                username = user.username,
                password = encoder.encode(user.password),
                enabled = user.isEnabled
            )
        )
    }

    override fun deleteUser(username: String?) {
        if (username == null) return
        runBlocking{ userDb.deleteByUsername(username) }
    }

    override fun changePassword(oldPassword: String?, newPassword: String?) {
        TODO("Not yet implemented")
    }

    override fun userExists(username: String?): Boolean {
        if (username == null) return false
        return runBlocking { userDb.existsByUsername(username).blockFirst()?:false }
    }
}
