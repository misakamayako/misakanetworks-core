package per.misaka.misakanetworkscore.dto

import jakarta.validation.constraints.NotNull


data class ArticleUploadDTO(
    @NotNull(message="标题不可为空")
    val title: String,
    val categories: List<Int>,
    val content: String
)
