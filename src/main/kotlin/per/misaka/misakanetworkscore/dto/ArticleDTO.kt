package per.misaka.misakanetworkscore.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotNull
import per.misaka.misakanetworkscore.enums.CategoryType
import java.time.LocalDateTime
import kotlin.Int


data class ArticleUploadDTO(
    @NotNull(message = "标题不可为空")
    val title: String,
    val content: String,
    val brief: String,
    val categories: List<Int>,
)

data class ArticleDTO(
    val title: String,
    val category: List<Int>,
    val content: String,
    val brief: String,
    val author: String?,
    val id: Int? = null,
    val views: Int? = 0
)

data class ArticlePreviewContent(val content: String)
data class ArticleDetailDTO(
    val id: Int,
    val title: String,
    val markdownUrl: String?,
    val htmlUrl: String?,
    val brief: String?,
    val author: String?,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    val createdAt: LocalDateTime?,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    val updatedAt: LocalDateTime?,
    @field:JsonIgnore
    val categories: String?,
    @field:JsonIgnore
    val categoryTypes: String?
) {
    @get:JsonProperty
    val category: List<CategoryDTO>
        get() {
            val ids = categories?.split(",") ?: emptyList()
            val strings = categoryTypes?.split(",") ?: emptyList()
            return ids.zip(strings) { id, type ->
                CategoryDTO(type, id.toInt(), CategoryType.Article)
            }
        }
}

data class ArticleBrief(
    val id: Int,
    val title: String,
    val version: Int,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    val createdAt: LocalDateTime?,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    val updatedAt: LocalDateTime?,
){
    @field:JsonProperty
    var views: Int = 0
}