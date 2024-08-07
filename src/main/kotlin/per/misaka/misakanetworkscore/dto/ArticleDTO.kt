package per.misaka.misakanetworkscore.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime


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
    @field:JsonIgnore
    val id: Int? = null,
    val views: Int? = 0
)

data class ArticlePreviewContent(val content: String)
data class QueryResultArticleDTO(
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
                CategoryDTO(type, id.toInt(), 2)
            }
        }
}

data class ArticleDetailDTO(
    val id: Int,
    val title: String,
    val brief: String?,
    var content: String,
    var categories: List<Int>? = null,
    var imgList: List<String>? = null
) {
    init {
        if (categories == null) categories = emptyList()
        if (imgList == null) imgList = emptyList()
    }
}