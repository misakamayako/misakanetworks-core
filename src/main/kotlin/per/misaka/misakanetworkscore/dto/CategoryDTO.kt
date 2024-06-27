package per.misaka.misakanetworkscore.dto

import jakarta.validation.constraints.NotBlank

data class CategoryDTO(
    @NotBlank(message = "名称不可为空")
    val category: String,
    val id: Int? = null,
    val type: Int?
)

@JvmInline
value class CategoryType(val type: Int?) {
    init {
        require(type != null) { "类型不可为空" }
        require(type in 1..3) { "不是有效的类型" }
        require(type.toString().isNotBlank()) { "类型不可为空" }
    }
}