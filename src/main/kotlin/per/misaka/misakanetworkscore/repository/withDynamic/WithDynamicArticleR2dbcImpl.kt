package per.misaka.misakanetworkscore.repository.withDynamic

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import per.misaka.misakanetworkscore.dto.ArticleBrief
import per.misaka.misakanetworkscore.dto.ArticleDetailDTO
import reactor.core.publisher.Flux
import java.time.LocalDateTime

@Repository
class WithDynamicArticleR2dbcImpl : WithDynamicArticleR2dbc {
    @Autowired
    private lateinit var databaseClient: DatabaseClient

    override fun findArticleByQuery(pageable: Pageable, title: String?, category: List<Int>?): Flux<ArticleBrief> {
        val query = StringBuilder("""
            select a.id as id,
                   a.title,
                   a.created_at,
                   a.updated_at,
                   ah.version as version
            from article a 
            left join article_to_category atc on a.id = atc.article 
            left join article_history ah on a.id = ah.article
            where a.has_delete = 0 and ah.status = 'published'
        """.trimIndent())

        val params = mutableMapOf<String, Any>()

        title?.let {
            query.append(" and a.title like :title")
            params["title"] = "%$it%"
        }

        category?.takeIf { it.isNotEmpty() }?.let {
            query.append(" and c.id in (:category)")
            params["category"] = it
        }

        query.append(" group by a.id")
        query.append(" limit :limit")
        params["limit"] = pageable.pageSize
        query.append(" offset :offset")
        params["offset"] = pageable.offset

        return databaseClient
            .sql(query.toString())
            .bindValues(params)
            .map { row, _ ->
                ArticleBrief(
                    id = row.get("id", Int::class.java)!!,
                    title = row.get("title", String::class.java)!!,
                    createdAt = row.get("created_at", LocalDateTime::class.java),
                    updatedAt = row.get("updated_at", LocalDateTime::class.java),
                    version = row.get("id", Int::class.java)!!,
                )
            }
            .all()
    }
}