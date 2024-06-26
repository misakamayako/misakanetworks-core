package per.misaka.misakanetworkscore.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("article")
data class ArticleEntity(
    @Id
    val id: Int? = null,

    @Column("title")
    val title: String,

    @Column("markdown_url")
    val markdownUrl: String,

    @Column("html_url")
    val htmlUrl: String,

    @Column("preview")
    val preview: String,

    @Column("author")
    val author: String,

    @Column("created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column("updated_at")
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

