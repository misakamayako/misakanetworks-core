package per.misaka.misakanetworkscore.exception.unofficialError


class MaliciousUserDetected(s: String? = null) : UnofficialError() {
    override val message = s ?: "MaliciousUserDetected"
    override val statusCode = 444
}
