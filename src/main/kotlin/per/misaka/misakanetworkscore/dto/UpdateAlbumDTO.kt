package per.misaka.misakanetworkscore.dto


data class UpdateAlbumDTO(
    val title:String?,
    val cover:String?,
    val categories:List<Int>?,
    val grading:Int?,
    val private:Boolean?
)
