package per.misaka.misakanetworkscore.component

import com.aliyun.auth.credentials.Credential
import com.aliyun.auth.credentials.provider.StaticCredentialProvider
import com.aliyun.sdk.service.sts20150401.AsyncClient
import com.aliyun.sdk.service.sts20150401.models.AssumeRoleRequest
import com.aliyun.sdk.service.sts20150401.models.AssumeRoleResponseBody
import darabonba.core.client.ClientOverrideConfiguration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import per.misaka.misakanetworkscore.ApplicationConfig

@Component
class AssumeRole {
    @Autowired
    private lateinit var applicationConfig: ApplicationConfig

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
            "{\"Version\":\"1\",\"Statement\":[{\"Action\":[\"oss:PutObject\"],\"Resource\":[\"acs:oss:*:*:misaka-temp-bucket/*\"],\"Effect\":\"Allow\"}]}"
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