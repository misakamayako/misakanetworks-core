package per.misaka.misakanetworkscore.component

import jakarta.annotation.PostConstruct
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.security.crypto.password.DelegatingPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder
import org.springframework.stereotype.Component

@Component
class CustomerDelegatingPasswordEncoder() : PasswordEncoder {
    private lateinit var  passwordEncoder: PasswordEncoder
    private val idForEncode = "pbkdf2@SpringSecurity_v5_8"

    @PostConstruct
    fun init() {
        val v58 = Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8()
        val hashMap = HashMap<String, PasswordEncoder>()
        hashMap["pbkdf2@SpringSecurity_v5_8"] = v58
        passwordEncoder = DelegatingPasswordEncoder(idForEncode, hashMap)
    }

    override fun encode(rawPassword: CharSequence?): String {
        return passwordEncoder.encode(rawPassword)
    }

    override fun matches(rawPassword: CharSequence?, encodedPassword: String?): Boolean {
        return passwordEncoder.matches(rawPassword, encodedPassword)
    }
}
