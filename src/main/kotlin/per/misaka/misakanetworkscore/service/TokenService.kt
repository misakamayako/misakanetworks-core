package per.misaka.misakanetworkscore.service

import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import per.misaka.misakanetworkscore.constants.CookieFor
import per.misaka.misakanetworkscore.dto.LoginUser
import java.util.UUID
import java.util.concurrent.TimeUnit

@Service
class TokenService {
    @Autowired
    private lateinit var redisTemplate: RedisTemplate<String,Any>

    fun storeUserDetail(userDetails: LoginUser,expirationTime:Long):String{
        val token = getToken()
        redisTemplate.opsForValue().set(token,userDetails, expirationTime,TimeUnit.MILLISECONDS)
        return token
    }
    fun getUserDetail(token:String):LoginUser?{
        return redisTemplate.opsForValue().get(token) as? LoginUser
    }
    fun removeToken(token:String){
        redisTemplate.delete(token)
    }
    private fun getToken():String{
        return UUID.randomUUID().toString()
    }

    fun getLoginUser(request: HttpServletRequest):LoginUser? {
        val name = ("__Secure-".takeIf { request.isSecure } ?: "") + CookieFor.Token.toString()
        val token = request.cookies?.find { cookie -> cookie.name==name }
        if (token==null||token.value.isNullOrEmpty()){
            return null
        }
        return getUserDetail(token.value)
    }
}