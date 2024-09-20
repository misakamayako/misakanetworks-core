package per.misaka.misakanetworkscore.component

import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import per.misaka.misakanetworkscore.config.ApplicationConfig
import java.nio.charset.StandardCharsets
import java.security.spec.KeySpec
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

@Component
class AesEncrypto {
    @Autowired
    private lateinit var applicationConfig: ApplicationConfig

    private lateinit var ivspec: IvParameterSpec
    private lateinit var secretKey: SecretKeySpec

    @PostConstruct
    private fun init() {
        val iv = byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        ivspec = IvParameterSpec(iv)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec: KeySpec = PBEKeySpec(
            applicationConfig.cryptoKey.toCharArray(), applicationConfig.cryptoSalt.toByteArray(), 65536, 256
        )
        secretKey = SecretKeySpec(factory.generateSecret(spec).encoded, "AES")
    }

    fun encrypt(strToEncrypt: String): String? {
        try {
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec)
            return Base64.getEncoder()
                .encodeToString(cipher.doFinal(strToEncrypt.toByteArray(StandardCharsets.UTF_8)))
        } catch (e: Exception) {
            println("Error occured during encryption: $e")
        }
        return null
    }


    fun decrypt(strToDecrypt: String): String {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec)
        return String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)))
    }

}

