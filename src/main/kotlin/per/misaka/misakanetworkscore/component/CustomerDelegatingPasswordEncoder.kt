package per.misaka.misakanetworkscore.component

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.*
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder

class CustomerDelegatingPasswordEncoder : PasswordEncoder {
    private var passwordEncoder: PasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()
    override fun encode(rawPassword: CharSequence?): String {
        val idForEncode = "bcrypt"
        val encoders = HashMap<String, PasswordEncoder>()
        encoders[idForEncode] = BCryptPasswordEncoder()
        encoders["noop"] = NoOpPasswordEncoder.getInstance()
        encoders["pbkdf2"] = Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_5()
        encoders["pbkdf2@SpringSecurity_v5_8"] = Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8()
        encoders["scrypt"] = SCryptPasswordEncoder.defaultsForSpringSecurity_v4_1()
        encoders["scrypt@SpringSecurity_v5_8"] = SCryptPasswordEncoder.defaultsForSpringSecurity_v5_8()
        encoders["argon2"] = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_2()
        encoders["argon2@SpringSecurity_v5_8"] = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8()
        encoders["sha256"] = StandardPasswordEncoder()

        val passwordEncoder: PasswordEncoder =
            DelegatingPasswordEncoder(idForEncode, encoders)
        TODO("Not yet implemented")
    }

    override fun matches(rawPassword: CharSequence?, encodedPassword: String?): Boolean {
        TODO("Not yet implemented")
    }
}
