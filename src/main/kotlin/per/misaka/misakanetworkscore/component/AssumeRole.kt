package per.misaka.misakanetworkscore.component

import com.aliyun.auth.credentials.Credential
import com.aliyun.auth.credentials.provider.StaticCredentialProvider
import com.aliyun.sdk.service.sts20150401.AsyncClient
import com.aliyun.sdk.service.sts20150401.models.AssumeRoleRequest
import com.aliyun.sdk.service.sts20150401.models.AssumeRoleResponseBody
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import darabonba.core.client.ClientOverrideConfiguration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import per.misaka.misakanetworkscore.config.ApplicationConfig
import per.misaka.misakanetworkscore.constants.OSSBucket

@Component
class AssumeRole {
    @Autowired
    private lateinit var applicationConfig: ApplicationConfig

    private val logger = LoggerFactory.getLogger(this::class.java)

    internal data class Policy(
        @get:JsonProperty("Version")
        val version: String,
        @get:JsonProperty("Statement")
        val statement: ArrayList<Statement>
    )

    internal data class Statement(
        @get:JsonProperty("Action")
        val action: List<String>,
        @get:JsonProperty("Effect")
        val effect: String,
        @get:JsonProperty("Resource")
        val resource: List<String>
    )

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
        val policy = Policy("1", arrayListOf())
        policy.statement.add(
            Statement(
                listOf("oss:PutObject", "oss:GetObject"),
                "Allow",
                listOf("acs:oss:*:*:${OSSBucket.Temp}/*")
            )
        )
        policy.statement.add(Statement(listOf("oss:GetObject"), "Allow", listOf("acs:oss:*:*:${OSSBucket.Article}/*")))
        val objectMapper = ObjectMapper()
        val policyString = objectMapper.writeValueAsString(policy)
        logger.info("new auth:{}", policyString)
        val assumeRoleRequest =
            AssumeRoleRequest.builder()
                .roleSessionName("3333")
                .durationSeconds(3600L)
                .roleArn(applicationConfig.tempRole)
                .policy(policyString)
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