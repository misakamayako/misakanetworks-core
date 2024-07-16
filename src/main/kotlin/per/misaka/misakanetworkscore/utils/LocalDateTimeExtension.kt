package per.misaka.misakanetworkscore.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
//import kotlin.time.Duration
//
//val Int.ms: Duration
//    get() = {
////        return Duration.milliseconds
//    }

fun LocalDateTime.format(format: String): String {
    val formatter = DateTimeFormatter.ofPattern(format)
    return this.format(formatter)
}
