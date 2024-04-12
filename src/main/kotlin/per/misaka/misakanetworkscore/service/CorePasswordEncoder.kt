package per.misaka.misakanetworkscore.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder
import org.springframework.stereotype.Service
import per.misaka.misakanetworkscore.exception.InternalServerException

@Service
class CorePasswordEncoder : PasswordEncoder {
    private  val logger: Logger = LoggerFactory.getLogger(CorePasswordEncoder::class.java)
    private val encoder: Pbkdf2PasswordEncoder = Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8()
    override fun encode(rawPassword: CharSequence?): String {
        if (rawPassword.isNullOrEmpty()) {
            throw InternalServerException("密码为空")
        }
        return encoder.encode(rawPassword)
    }

    override fun matches(rawPassword: CharSequence?, encodedPassword: String?): Boolean {
        if (rawPassword == null || encodedPassword == null) {
            return false
        }
        return encoder.matches(rawPassword, encodedPassword)
    }
}
