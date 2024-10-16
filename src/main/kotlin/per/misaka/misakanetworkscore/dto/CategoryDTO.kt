package per.misaka.misakanetworkscore.dto

import jakarta.validation.constraints.NotBlank
import per.misaka.misakanetworkscore.enums.CategoryType
import kotlin.Int

data class CategoryDTO(
    @NotBlank(message = "名称不可为空")
    val category: String,
    val id: Int? = null,
    val type: CategoryType
)
