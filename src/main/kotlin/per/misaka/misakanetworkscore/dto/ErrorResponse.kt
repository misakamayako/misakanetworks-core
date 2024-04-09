package per.misaka.misakanetworkscore.dto

data class ErrorResponse(
    val status:Int,
    val message:String?="unknown error"
)
