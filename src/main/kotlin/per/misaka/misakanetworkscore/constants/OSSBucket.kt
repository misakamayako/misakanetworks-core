package per.misaka.misakanetworkscore.constants

enum class OSSBucket(val value: String) {
    Article("misaka-networks-article"){
        override fun toString(): String {
            return "misaka-networks-article"
        }
    },
    Temp("misaka-networks-temp"){
        override fun toString(): String {
            return "misaka-networks-temp"
        }
    }
}