package per.misaka.misakanetworkscore.entity

import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("article_to_category")
data class ArticleToCategoryEntity(
    @Column("article")
    val article: Long,
    @Column("category")
    val category: Int,
)
