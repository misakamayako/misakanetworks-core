package per.misaka.misakanetworkscore.dto

data class ErrorResponseDto(
    val errorMessage: String,
    val httpStatus: Int,
)
