package per.misaka.misakanetworkscore.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.withContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import per.misaka.misakanetworkscore.entity.UserEntity
import per.misaka.misakanetworkscore.exception.AuthoritiesException
import per.misaka.misakanetworkscore.exception.BadRequestException
import per.misaka.misakanetworkscore.repository.UserRepository

@Service
class UserService(
    private val userDB: UserRepository,
    private val encoder: CorePasswordEncoder
) {
    val logger: Logger = LoggerFactory.getLogger(UserService::class.java)

    suspend fun resign(account: String, password: String) = withContext(Dispatchers.IO) {
        logger.info("try create new account, account name:$account")
        val check = userDB.findByUsername(account).awaitSingle()
        if (check != null) {
            throw BadRequestException("用户已被注册")
        }
        val userEntity = UserEntity(username = account, password = encoder.encode(password), enabled = true)
        userDB.save(userEntity)
    }

    suspend fun login(account: String, password: String): Int {
        val user = try {
            userDB.findByUsername(account).awaitSingle()
        } catch (e: Exception) {
            logger.debug(e.message)
            throw InternalError("登录失败")
        }
        logger.debug("login, user name:{}, account name:{}", account, user)
        return if (user != null && user.enabled) {
            if (encoder.matches(password, user.password)) {
                user.id!!
            } else {
                throw AuthoritiesException("密码错误")
            }
        } else {
            throw AuthoritiesException("用户不存在或被禁用")
        }
    }
}
