package per.misaka.misakanetworkscore

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource

@Configuration
@PropertySource("classpath:secret.properties")
class ApplicationConfig {

    @Value("\${aliyun.OSS.accessKeyId}")
    lateinit var accessKeyId: String

    @Value("\${aliyun.OSS.accessKeySecret}")
    lateinit var accessKeySecret: String

    @Value("\${aliyun.OSS.tempUser.accessKeyId}")
    lateinit var tempAccessKeyId: String
    @Value("\${aliyun.OSS.tempUser.accessKeySecret}")
    lateinit var tempAccessKeySecret: String
    @Value("\${aliyun.OSS.tempUser.role}")
    lateinit var tempRole: String

    @Value("\${crypto.key}")
    lateinit var cryptoKey: String
    @Value("\${crypto.salt}")
    lateinit var cryptoSalt: String

    @Value("\${aliyun.OSS.EndPoint}")
    lateinit var endpoint:String
}
