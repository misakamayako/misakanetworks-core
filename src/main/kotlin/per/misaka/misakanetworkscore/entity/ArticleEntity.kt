package per.misaka.misakanetworkscore.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("article")
data class ArticleEntity(
    @Id
    val id: Long? = null,

    @Column("title")
    val title: String,

    @Column("markdown_url")
    val markdownUrl: String,

    @Column("html_url")
    val htmlUrl: String,

    @Column("brief")
    val brief: String,

    @Column("author")
    val author: String,

    val views:Int=0,

    @Column("created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column("updated_at")
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

