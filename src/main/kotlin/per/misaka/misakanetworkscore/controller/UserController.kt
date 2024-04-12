package per.misaka.misakanetworkscore.controller

import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import per.misaka.misakanetworkscore.dto.UserDTO
import per.misaka.misakanetworkscore.exception.unofficialError.MaliciousUserDetected
import per.misaka.misakanetworkscore.service.RedisService
import per.misaka.misakanetworkscore.service.UserService
import java.util.*
import java.util.concurrent.TimeUnit

@RestController
final class UserController(private val userService: UserService, private val redisService: RedisService) {
    private val logger = LoggerFactory.getLogger(UserController::class.java)

    @PostMapping("/login")
    suspend fun userLogin(
        @RequestBody @Validated data: UserDTO,
        @CookieValue(name = "token") currentToken: String?
    ): ResponseEntity<Void> {
        logger.info("new login request as \"${data.username}\"")
        val userId = userService.login(data.username, data.password)
        if (currentToken != null) {
            redisService.removeEntity(currentToken)
        }
        val uuid = UUID.randomUUID().toString()
        redisService.saveEntity(userId, uuid, 1L, TimeUnit.HOURS)
        val ck = ResponseCookie.from("token", uuid)
        ck.httpOnly(true)
        val headers = HttpHeaders()
        headers.set(HttpHeaders.SET_COOKIE, ck.build().toString())
        return ResponseEntity.ok().headers(headers).build()
    }

    @PostMapping("/test444")
    fun test444() {
        throw MaliciousUserDetected()
    }
}
