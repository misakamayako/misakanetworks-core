package per.misaka.misakanetworkscore.dto

import jakarta.validation.constraints.NotEmpty

data class CreateAlbumDTO(
    @NotEmpty
    val title:String,
    val cover:String?,
    val categories:List<Int>?,
    val grading:Int?,
    @Transient
    val private:Boolean?
)
