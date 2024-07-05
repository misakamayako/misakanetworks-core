package per.misaka.misakanetworkscore.dto

import jakarta.validation.constraints.NotNull


data class ArticleUploadDTO(
    @NotNull(message = "标题不可为空")
    val title: String,
    val brief:String,
    val categories: List<Int>,
    val content: String
)

data class ArticleDTO(
    val title: String,
    val category: List<Int>,
    val content: String,
    @Transient
    val id: Int,
    val views: Int
)

data class ArticlePreviewContent(val content:String)
data class QueryResultArticleDTO(
    val id: Long,
    val title: String,
    val markdownUrl: String,
    val htmlUrl: String,
    val brief: String,
    val author: String,
    val createdAt: String,
    val updatedAt: String,
    val categories: List<String>,
    val categoryTypes: List<String>
)
