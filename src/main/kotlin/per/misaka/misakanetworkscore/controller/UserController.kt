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
        val httpHeaders = HttpHeaders()
        ResponseCookie.from("token", uuid)
            .path("/")
            .maxAge(3600)
            .httpOnly(true)
            .build()
            .let { httpHeaders.set(HttpHeaders.SET_COOKIE, it.toString()) }
        return ResponseEntity.noContent().headers(httpHeaders).build()
    }

    @PostMapping("/logoutHandler")
    suspend fun userLogout(@CookieValue(name = "token") token: String): ResponseEntity<Void> {
        val httpHeaders = HttpHeaders()
        if (token.isNotEmpty()) {
            val userId = redisService.getEntity(token, Int::class)
            logger.info("new logout request as \"${userId}\"")
            redisService.removeEntity(token)
            ResponseCookie.from("token", token)
                .path("/")
                .maxAge(0)
                .httpOnly(true)
                .build()
                .let { httpHeaders.set(HttpHeaders.SET_COOKIE, it.toString()) }
        }
        return ResponseEntity.noContent().headers(httpHeaders).build()
    }
}
