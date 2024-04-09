package per.misaka.misakanetworkscore.dto

import jakarta.validation.constraints.NotBlank

data class CategoryDTO(
    @NotBlank(message = "名称不可为空")
    val category: String,
    val id: Int? = null,
    val type: Int?
)
