package per.misaka.misakanetworkscore.utils

import per.misaka.misakanetworkscore.ApplicationConfig
import java.nio.charset.StandardCharsets
import java.security.spec.KeySpec
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec


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

private val iv = byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
private val ivspec = IvParameterSpec(iv)

private val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")

val spec: KeySpec = PBEKeySpec(
    ApplicationConfig().cryptoKey.toCharArray(),  ApplicationConfig().cryptoSalt.toByteArray(), 65536, 256
)
val secretKey = SecretKeySpec(factory.generateSecret(spec).encoded, "AES")

fun decrypt(strToDecrypt: String): String {
    val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
    cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec)
    return String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)))
}
