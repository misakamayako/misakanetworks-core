package per.misaka.misakanetworkscore.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table(name = "img_to_category")
data class ImgToCategoryEntity(
    @Id
    var id:Int?=null,

    @Column("img_id")
    var imgId: Int,

    @Column("category_id")
    var categoryId: Int
)
