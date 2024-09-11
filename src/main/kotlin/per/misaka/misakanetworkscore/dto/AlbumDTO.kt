package per.misaka.misakanetworkscore.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import per.misaka.misakanetworkscore.enums.CategoryType
import java.time.LocalDateTime

data class AlbumBrief(
    val id: Int,
    val name: String,
    val description: String,
    val coverURL: String,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    val createAt: LocalDateTime,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    val updateAt: LocalDateTime,
    val contentExplicitContent: Boolean,
    @JsonIgnore
    val hasDelete: Boolean,
    @JsonIgnore
    val isPrivate: Boolean
)
data class AlbumDetail(
    val id: Int,
    val name: String,
    val description: String,
    val coverURL: String,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    val createAt: LocalDateTime,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    val updateAt: LocalDateTime,
    val contentExplicitContent: Boolean,
    @JsonIgnore
    val hasDelete: Boolean,
    val isPrivate: Boolean,
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
                CategoryDTO(type, id.toInt(), CategoryType.Album)
            }
        }
}
data class AlbumDTO(
    val id:Int?,
    val coverId:Int?,
    val name:String,
    val tags:List<Int>,
    val isPrivate:Boolean,
    val description:String,
    val containsExplicitContent:Boolean
)