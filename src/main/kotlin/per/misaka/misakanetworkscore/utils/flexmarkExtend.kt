package per.misaka.misakanetworkscore.utils

import com.vladsch.flexmark.ast.Image
import com.vladsch.flexmark.util.ast.Visitor
import com.vladsch.flexmark.util.sequence.BasedSequence
import java.net.URI
import java.net.URISyntaxException
import java.util.*

class ImageSourceReplacer private constructor(
    private val oldHost: String,
    private val newHost: String,
    private val folder: String
) :
    Visitor<Image> {
    data class Builder(
        var oldHost: String? = null,
        var newHost: String? = null,
        var folder: String? = null
    ) {
        fun build(): ImageSourceReplacer {
            require(oldHost != null) { "oldHost must not be null" }
            require(newHost != null) { "newHost must not be null" }
            require(folder != null) { "folder must not be null" }
            return ImageSourceReplacer(oldHost!!, newHost!!, folder!!)
        }
    }

    val imgUrls = HashMap<String, Record>()

    class Record internal constructor(val host: String, val key: String) {
        override fun hashCode(): Int {
            return toString().hashCode()
        }

        override fun toString(): String {
            return "//$host/$key"
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            if (!super.equals(other)) return false

            other as Record

            if (host != other.host) return false
            if (key != other.key) return false

            return true
        }
    }

    companion object {
        fun create(block: Builder.() -> Unit): ImageSourceReplacer {
            return Builder().apply(block).build()
        }
    }

    private fun generateUniqueFileName(extension: String): String {
        val uuid = UUID.randomUUID().toString()
        return "$uuid.$extension"
    }

    override fun visit(node: Image) {
        val uri = try {
            URI(node.url.toString())
        } catch (_: URISyntaxException) {
            return
        }
        if (uri.host == oldHost) {
            val key =
                imgUrls.getOrPut(node.url.toString()) {
                    Record("$newHost/$folder", generateUniqueFileName(uri.path.substringAfterLast(".")))
                }
            node.url = BasedSequence.of(key.toString())
        }
    }
}
