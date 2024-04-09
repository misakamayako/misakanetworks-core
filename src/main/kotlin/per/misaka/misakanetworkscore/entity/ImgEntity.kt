package per.misaka.misakanetworkscore.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table


@Table("img")
data class ImgEntity(
    @Id
    var id: Int? = null,

    val eigenvalues: String,

    val name: String,

    val grading: Int = 1,

    var private: Boolean = false,

    var album: Int? = null,
)
