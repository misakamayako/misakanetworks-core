package per.misaka.misakanetworkscore.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("article")
data class ArticleEntity(
    @Id
    var id: Int? = null,
    val title: String,
    val brief: String?,
    @Column("createAt")
    @CreatedDate
    val createAt: LocalDateTime?,
    val views: Int,
)
