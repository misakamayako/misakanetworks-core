package per.misaka.misakanetworkscore.repository

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import per.misaka.misakanetworkscore.dto.QueryResultArticleDTO
import per.misaka.misakanetworkscore.entity.ArticleEntity
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface ArticleRepository : ReactiveCrudRepository<ArticleEntity, Long> {
    @Query("""
        select * from article where title = :title and hasDelete = 0
    """)
    fun findByTitle(title: String): Mono<ArticleEntity?>

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
    GROUP_CONCAT(c.description ORDER BY c.description SEPARATOR ',') AS categories,
    GROUP_CONCAT(c.id ORDER BY c.id SEPARATOR ',') AS category_types
FROM
    article a
        LEFT JOIN
    article_to_category atc ON a.id = atc.article
        LEFT JOIN
    category c ON atc.category = c.id
    where a.hasDelete = 0
GROUP BY
    a.id
limit :limit offset :offset
    """)
    fun queryByPage(@Param("page") limit:Int,@Param("offset") offset:Int): Flux<QueryResultArticleDTO>
    @Query("""
        select count(*) from article where hasDelete = 0
    """)
    override fun count(): Mono<Long>

    @Query("""
        update article set hasDelete = 1,updated_at=now() where id =:id 
    """)
    fun deleteArticleById(id: Long): Mono<Void>

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
    GROUP_CONCAT(c.description ORDER BY c.description SEPARATOR ',') AS categories,
    GROUP_CONCAT(c.id ORDER BY c.id SEPARATOR ',') AS category_types
FROM
    article a
        LEFT JOIN
    article_to_category atc ON a.id = atc.article
        LEFT JOIN
    category c ON atc.category = c.id
    where a.hasDelete = 0 and a.id= :id
GROUP BY
    a.id
    """)
    fun getArticleDetailById(@Param("id") id:Long):Mono<QueryResultArticleDTO?>
}
