package per.misaka.misakanetworkscore.service

import com.aliyun.oss.ClientBuilderConfiguration
import com.aliyun.oss.OSS
import com.aliyun.oss.OSSClientBuilder
import com.aliyun.oss.OSSException
import com.aliyun.oss.common.comm.Protocol
import com.aliyun.oss.model.CopyObjectResult
import com.aliyun.oss.model.OSSObject
import com.aliyun.oss.model.ObjectMetadata
import com.aliyun.oss.model.PutObjectResult
import com.aliyun.oss.model.VoidResult
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import per.misaka.misakanetworkscore.config.ApplicationConfig
import per.misaka.misakanetworkscore.component.AesEncrypto
import per.misaka.misakanetworkscore.constants.OSSBucket
import per.misaka.misakanetworkscore.constants.OSSBucketURL
import per.misaka.misakanetworkscore.exception.NotFoundException
import java.io.InputStream


@Service
class OSSService {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @Autowired
    private lateinit var applicationConfig: ApplicationConfig

    @Autowired
    private lateinit var localOSSBucketURL: OSSBucketURL

    @Autowired
    private lateinit var aesEncrypto: AesEncrypto

    private lateinit var ossClient: OSS

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    @PostConstruct
    private fun initOSS() {
        val accessKeyId = aesEncrypto.decrypt(applicationConfig.accessKeyId)
        val accessKeySecret = aesEncrypto.decrypt(applicationConfig.accessKeySecret)
        val endpoint = applicationConfig.endpoint
        val config = ClientBuilderConfiguration()
        with(config) {
            protocol = Protocol.HTTPS
            userAgent = "aliyun-sdk-kotlin"
        }
        ossClient = OSSClientBuilder().build("https://$endpoint", accessKeyId, accessKeySecret, config)
        scope.launch {
            processRequests()
        }
    }

    private val requestChannel = Channel<suspend () -> Unit>(capacity = 25)
    private suspend fun submit(request: suspend () -> Unit) {
        requestChannel.send(request)
    }

    private suspend fun processRequests() {
        for (request in requestChannel) {
            try {
                request()
            } catch (e: Exception) {
                logger.error(e.message,e)
            }
        }
    }

    @PreDestroy
    private fun closeGracefully() {
        runBlocking {
            logger.info("Shutting down OSSService, waiting for pending tasks to complete.")
            requestChannel.close()
            job.cancelAndJoin()
            ossClient.shutdown()
            logger.info("OSSService shutdown complete.")
        }
    }

    suspend fun putObject(bucketName: OSSBucket, key: String, input: InputStream,metaData:ObjectMetadata?=null): Deferred<PutObjectResult> {
        logger.info("Putting object: $bucketName:$key")
        val result = CompletableDeferred<PutObjectResult>()
        submit {
            try {
                val putResult = ossClient.putObject(bucketName.value, key, input,metaData)
                result.complete(putResult)
            } catch (e: Exception) {
                result.completeExceptionally(e)
                logger.error(e.message,e)
            }
        }
        return result
    }

    suspend fun deleteObject(bucketName: OSSBucket, key: String): Deferred<VoidResult> {
        logger.info("Deleting object: $bucketName/$key")
        val result = CompletableDeferred<VoidResult>()
        submit {
            try {
                val deleteResult = ossClient.deleteObject(bucketName.toString(), key)
                result.complete(deleteResult)
            } catch (e: Exception) {
                result.completeExceptionally(e)
                logger.error(e.message,e)
            }
        }
        return result
    }

    suspend fun copyObject(
        sourceBucketName: OSSBucket, sourceKey: String,
        destinationBucketName: OSSBucket, destinationKey: String
    ): Deferred<CopyObjectResult> {
        logger.info("copy object from ${sourceBucketName.value}/$sourceKey -> ${destinationBucketName.value}/$destinationKey")
        val result = CompletableDeferred<CopyObjectResult>()
        submit {
            try {
                val copyResult =
                    ossClient.copyObject(sourceBucketName.value, sourceKey, destinationBucketName.value, destinationKey)
                result.complete(copyResult)
            } catch (e: Exception) {
                result.completeExceptionally(e)
                logger.error("copy object from ${sourceBucketName.value}/$sourceKey -> ${destinationBucketName.value}/$destinationKey")
                logger.error("copy object error: ${e.message}",e)
            }
        }
        return result
    }
    suspend fun getObject(bucket:OSSBucket,key:String):Deferred<OSSObject>{
        logger.info("get object at ${bucket.value}/$key")
        val result = CompletableDeferred<OSSObject>()
        submit {
            try{
                val getResult = ossClient.getObject(bucket.value,key)
                result.complete(getResult)
            } catch (e:Exception){
                result.completeExceptionally(e)
                logger.error("get object failed: ${e.message}",e)
                if (e is OSSException&&e.errorCode==="noSuchKey"){
                    throw NotFoundException("no object named $key")
                }
            }
        }
        return result
    }

    fun getBucketURL(bucket: OSSBucket): String {
        return when (bucket) {
            OSSBucket.Article -> localOSSBucketURL.ossArticleBucketUrl
            OSSBucket.Temp -> localOSSBucketURL.ossTempBucketUrl
            else -> ""
        }
    }
}