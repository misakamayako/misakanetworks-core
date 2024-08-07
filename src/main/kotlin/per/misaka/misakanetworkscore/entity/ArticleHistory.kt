package per.misaka.misakanetworkscore.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("article_history")
data class ArticleHistory(
    @Id
    val id: Int? = null,

    val article: Int,

    val version:Int,

    @Column("created_at")
    val createAt: LocalDateTime?=null,

    @Column("updated_at")
    val updateAt: LocalDateTime?=null,

    val status:ArticleStatus = ArticleStatus.published
)

enum class ArticleStatus{
    drift,
    published,
    archived
}