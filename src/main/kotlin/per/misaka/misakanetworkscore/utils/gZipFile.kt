package per.misaka.misakanetworkscore.utils

import org.apache.logging.log4j.LogManager
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.zip.GZIPOutputStream

private val logger = LogManager.getLogger("topLevel")

fun gzipFile(byteArrayInputStream: ByteArrayInputStream): InputStream {
    val originalSize = byteArrayInputStream.available()
    logger.info("original file size: {} bytes", originalSize)
    val byteArrayOutStream = ByteArrayOutputStream()
    GZIPOutputStream(byteArrayOutStream).use { gzipOutStream ->
        byteArrayInputStream.copyTo(gzipOutStream)
    }
    val compressedData = byteArrayOutStream.toByteArray()
    val compressedSize = compressedData.size
    logger.info("gzipped file size: {} bytes", compressedSize)
    val reduction = originalSize - compressedSize
    val reductionPercentage = (reduction.toDouble() / originalSize) * 100
    val formattedPercentage = String.format("%.2f", reductionPercentage)
    logger.info("file size reduced by: {}% ({} bytes)", formattedPercentage, reduction)
    return ByteArrayInputStream(compressedData)
}