package per.misaka.misakanetworkscore.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("album")
data class AlbumEntity(
    @Id
    var id: Int? = null,
    var title: String? = null,
    var cover: String? = null,
    var grading: Int? = null,
    var private: Boolean? = null
)

