package per.misaka.misakanetworkscore.dto

import jakarta.validation.constraints.NotNull
import kotlin.Int

data class ImgUploadDTO(
    @NotNull(message="文件地址不能为空")
    val fileUrl: String,
    val name: String,
    val categories: List<Int>? = null,
    val album: Int? = null,
    val grading: Int = 1,
    val private: Boolean = false
)
