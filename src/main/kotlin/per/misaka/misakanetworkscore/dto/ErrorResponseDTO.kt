package per.misaka.misakanetworkscore.dto

import kotlin.Int

data class ErrorResponseDto(
    val errorMessage: String,
    val httpStatus: Int,
)
