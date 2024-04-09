package per.misaka.misakanetworkscore.utils

import com.aliyun.oss.OSS
import com.aliyun.oss.OSSClientBuilder
import per.misaka.misakanetworkscore.ApplicationConfig
import kotlin.reflect.KProperty


val OSSInstance by AliOSS

object AliOSS {
	private const val endpoint = "oss-cn-shanghai.aliyuncs.com"
	private val customProperties:ApplicationConfig = ApplicationConfig()
	@JvmStatic
	val accessKeyId = decrypt(customProperties.accessKeyId)

	@JvmField
	val accessKeySecret = decrypt(customProperties.accessKeySecret)

	@get:Synchronized
	private var ossClient: OSS? = null
		get() {
			if (field == null) {
				field = OSSClientBuilder().build("https://$endpoint", accessKeyId, accessKeySecret)
			}
			return field
		}

	operator fun getValue(nothing: Nothing?, property: KProperty<*>): OSS = ossClient!!
}
