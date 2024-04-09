package per.misaka.misakanetworkscore.entity

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Reference
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table(name = "album_to_category")
data class AlbumToCategoryEntity(
    @Id
    var id:Int?=null,

    @Column("album_id")
//    @Reference(value = Int::class, to = AlbumEntity::class)
    var albumId: Int,

    @Column("category_id")
//    @Reference(value = Int::class, to = CategoryEntity::class)
    var categoryId: Int
)
