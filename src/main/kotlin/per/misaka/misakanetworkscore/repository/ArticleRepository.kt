package per.misaka.misakanetworkscore.repository

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.jdbc.core.RowMapper
import per.misaka.misakanetworkscore.entity.ArticleEntity
import reactor.core.publisher.Flux

interface ArticleRepository : ReactiveCrudRepository<ArticleEntity, Int> {
    suspend fun findByTitle(title: String): ArticleEntity?

    @Query("""
        SELECT
    a.id,
    a.title,
    a.markdown_url,
    a.html_url,
    a.brief,
    a.author,
    a.created_at,
    a.updated_at,
    GROUP_CONCAT(c.description ORDER BY c.description SEPARATOR ', ') AS categories,
    GROUP_CONCAT(c.id ORDER BY c.id SEPARATOR ', ') AS category_types
FROM
    article a
        LEFT JOIN
    article_to_category atc ON a.id = atc.article
        LEFT JOIN
    category c ON atc.category = c.id
GROUP BY
    a.id,
    a.title,
    a.markdown_url,
    a.html_url,
    a.brief,
    a.author,
    a.created_at,
    a.updated_at
limit :page offset :offset
    """)
    suspend fun queryByPage(@Param("page") page:Int,@Param("offset") offset:Int){
        TODO("确定返回值类型")
    }
}
