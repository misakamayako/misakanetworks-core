package per.misaka.misakanetworkscore.service

import com.aliyun.sdk.service.sts20150401.models.AssumeRoleResponseBody.Credentials
import org.springframework.stereotype.Service
import per.misaka.misakanetworkscore.dto.STSDTO
import per.misaka.misakanetworkscore.exception.InternalServerException
import per.misaka.misakanetworkscore.utils.getAssumeRole
import java.util.concurrent.TimeUnit

@Service
class FileService(private val redisService:RedisService) {
    suspend fun getSTS(): STSDTO {
        val lastSTS:STSDTO? = redisService.getEntity("sts",STSDTO::class)
        if (lastSTS!=null){
            return lastSTS
        }
        val newSTS =  getAssumeRole().await() ?.let {
            STSDTO(it.accessKeyId,it.accessKeySecret,it.expiration,it.securityToken)
        }?: throw InternalServerException("获取sts失败")
        redisService.saveEntity(newSTS, "sts", 3600, TimeUnit.SECONDS)
        return newSTS
    }
}
