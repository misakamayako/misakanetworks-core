package per.misaka.misakanetworkscore.component

import com.aliyun.auth.credentials.Credential
import com.aliyun.auth.credentials.provider.StaticCredentialProvider
import com.aliyun.sdk.service.sts20150401.AsyncClient
import com.aliyun.sdk.service.sts20150401.models.AssumeRoleRequest
import com.aliyun.sdk.service.sts20150401.models.AssumeRoleResponseBody
import darabonba.core.client.ClientOverrideConfiguration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import per.misaka.misakanetworkscore.ApplicationConfig
import per.misaka.misakanetworkscore.constants.OSSBucket

@Component
class AssumeRole {
    @Autowired
    private lateinit var applicationConfig: ApplicationConfig

    private val logger =  LoggerFactory.getLogger(this::class.java)

    suspend fun getAliAssumeRole(): AssumeRoleResponseBody.Credentials? {
        val provider = StaticCredentialProvider.create(
            Credential.builder()
                .accessKeyId(applicationConfig.tempAccessKeyId)
                .accessKeySecret(applicationConfig.tempAccessKeySecret)
                .build()
        )
        val client = AsyncClient.builder()
            .region("cn-shanghai")
            .credentialsProvider(provider)
            .overrideConfiguration(
                ClientOverrideConfiguration.create()
                    .setEndpointOverride("sts.cn-shanghai.aliyuncs.com")
            )
            .build()
        val policy =
            "{\"Version\":\"1\",\"Statement\":[{\"Action\":[\"oss:PutObject\",\"oss:GetObject\"],\"Resource\":[\"acs:oss:*:*:${OSSBucket.Temp.value}/*\"],\"Effect\":\"Allow\"}]}"
        logger.info("new auth:$policy")
        val assumeRoleRequest =
            AssumeRoleRequest.builder()
                .roleSessionName("3333")
                .durationSeconds(3600L)
                .roleArn(applicationConfig.tempRole)
                .policy(policy)
                .build()
        return try {
            withContext(Dispatchers.IO) {
                client.assumeRole(assumeRoleRequest).get()
            }.body.credentials
        } catch (e: Exception) {
            println(e.message)
            null
        } finally {
            client.close()
        }
    }
}