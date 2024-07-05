package per.misaka.misakanetworkscore.constants

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.PropertySource
import org.springframework.stereotype.Component

@Component
@PropertySource("classpath:secret.properties")
class OSSBucketURL {
    @Value("\${aliyun.OSS.Bucket.temp.outer}")
    lateinit var ossTempBucketUrl: String
    @Value("\${aliyun.OSS.Bucket.article.outer}")
    lateinit var ossArticleBucketUrl: String
}