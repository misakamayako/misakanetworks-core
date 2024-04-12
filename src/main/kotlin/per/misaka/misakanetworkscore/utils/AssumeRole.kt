package per.misaka.misakanetworkscore.utils

import com.aliyun.auth.credentials.Credential
import com.aliyun.auth.credentials.provider.StaticCredentialProvider
import com.aliyun.sdk.service.sts20150401.AsyncClient
import com.aliyun.sdk.service.sts20150401.models.AssumeRoleRequest
import darabonba.core.client.ClientOverrideConfiguration
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import per.misaka.misakanetworkscore.ApplicationConfig

suspend fun getAssumeRole() = coroutineScope {
    val applicationConfig = ApplicationConfig()
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

    val assumeRoleRequest =
        AssumeRoleRequest.builder()
            .roleSessionName("3333")
            .durationSeconds(3600L)
            .roleArn(applicationConfig.tempRole)
            .build()

    async {
        try {
            client.assumeRole(assumeRoleRequest).get().body.credentials
        } catch (e: Exception) {
            println(e.message)
            null
        } finally {
            client.close()
        }
    }
}
