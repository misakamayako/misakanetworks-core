package per.misaka.misakanetworkscore.dto

import kotlin.Int

data class ApiResponse<T>(
    val code: Int,
    val message: String,
    val data: T? = null
)
