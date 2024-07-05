package per.misaka.misakanetworkscore.utils

fun String.getURLPath(): String? {
    val start = this.indexOf("//")
    if (start == -1) {
        return null
    }
    return this.substring(this.indexOf("/", start + 1))
}