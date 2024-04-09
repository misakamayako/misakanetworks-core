package per.misaka.misakanetworkscore.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import per.misaka.misakanetworkscore.entity.UserEntity
import per.misaka.misakanetworkscore.exception.BadRequestException
import per.misaka.misakanetworkscore.repository.UserRepository

@Service
class UserService(
    private val userDB: UserRepository,
    private val encoder: CorePasswordEncoder
) {
    companion object {
        @JvmStatic
        val logger: Logger = LoggerFactory.getLogger(UserService::class.java)
    }

    suspend fun resign(account: String, password: String) = withContext(Dispatchers.IO) {
        logger.info("try create new account, account name:$account")
        val check = userDB.findByUsername(account)
        if (check != null) {
            throw BadRequestException("用户已被注册")
        }
        val userEntity = UserEntity(username = account, password = encoder.encode(password), enabled = true)
        userDB.save(userEntity)
    }

    suspend fun login(account: String, password: String): Int = withContext(Dispatchers.IO) {
        val user = userDB.findByUsername(account) ?: return@withContext -1
        if(user.enabled && encoder.matches(password, user.password)){
            user.id!!
        } else {
            -1
        }
    }
}
