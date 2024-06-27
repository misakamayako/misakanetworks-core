package per.misaka.misakanetworkscore.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import per.misaka.misakanetworkscore.component.AssumeRole
import per.misaka.misakanetworkscore.dto.STSDTO
import per.misaka.misakanetworkscore.exception.InternalServerException
import java.util.concurrent.TimeUnit

@Service
class FileService {
    @Autowired
    private lateinit var redisService: RedisService

    @Autowired
    private lateinit var assumeRole: AssumeRole
    suspend fun getSTS(): STSDTO {
        val lastSTS: STSDTO? = redisService.getEntity("sts", STSDTO::class)
        if (lastSTS != null) {
            return lastSTS
        }
        val newSTS = assumeRole.getAliAssumeRole()?.let {
            STSDTO(it.accessKeyId, it.accessKeySecret, it.expiration, it.securityToken)
        } ?: throw InternalServerException("获取sts失败")
        redisService.saveEntity(newSTS, "sts", 3600, TimeUnit.SECONDS)
        return newSTS
    }
}
