package per.misaka.misakanetworkscore.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("article")
data class ArticleEntity(
    @Id
    val id: Int? = null,

    val title:String,

    val folder:String,

    val brief:String,

    val views:Int?=0,

    val author:String?="@misakamaiyako",

    @Column("has_delete")
    val hasDelete:Boolean?=false,

    @Column("created_at")
    val createAt:LocalDateTime?=LocalDateTime.now(),

    @Column("updated_at")
    val updateAt:LocalDateTime?=LocalDateTime.now(),
)

