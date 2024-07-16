package per.misaka.misakanetworkscore.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.validation.constraints.NotEmpty

data class CreateAlbumDTO(
    @NotEmpty
    val title:String,
    val cover:String?,
    val categories:List<Int>?,
    val grading:Int?,
    @field:JsonIgnore
    val private:Boolean?
)
