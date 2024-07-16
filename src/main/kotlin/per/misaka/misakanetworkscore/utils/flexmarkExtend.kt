package per.misaka.misakanetworkscore.utils

import com.vladsch.flexmark.ast.Image
import com.vladsch.flexmark.util.ast.Visitor
import com.vladsch.flexmark.util.sequence.BasedSequence
import per.misaka.misakanetworkscore.constants.OSSBucket
import java.net.URI
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class ImageSourceReplacer(private val newBase: String) : Visitor<Image> {
    val imgUrls = HashMap<String, String>()
    private val currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("YY-MM-dd"))
    private fun generateUniqueFileName(originalFileName: String): String {
        val uuid = UUID.randomUUID().toString()
        val extension = originalFileName.substringAfterLast(".")
        return "$uuid-$currentDate.$extension"
    }

    override fun visit(node: Image) {
        val oldPath = URI(node.url.toString()).path
        if (!oldPath.startsWith('/' + OSSBucket.Temp.value)) return
        val oldKey = oldPath.substringAfter('/')
        val key = imgUrls.getOrPut(oldKey) { generateUniqueFileName(oldKey) }
        val newSource = "//$newBase/$key"
        node.url = BasedSequence.of(newSource)
        imgUrls[oldKey] = key
    }
}
