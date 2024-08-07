package per.misaka.misakanetworkscore.repository

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.repository.query.Param
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import per.misaka.misakanetworkscore.dto.QueryResultArticleDTO
import per.misaka.misakanetworkscore.entity.ArticleEntity
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface ArticleRepository : ReactiveCrudRepository<ArticleEntity, Int> {

    @Query(
        """
select a.id as id,
        a.title,
        CONCAT('/',a.folder,'/v',ah.version,'.md') as markdownUrl,
        CONCAT('/',a.folder,'/v',ah.version,'.fragment') as htmlUrl,
        a.brief,
        a.author,
        a.created_at,
        group_concat(c.id) as categories,
        group_concat(c.description) as categoryTypes
        from article a 
        left join article_to_category atc on a.id = atc.article 
        left join category c on atc.category = c.id
        left join article_history ah on a.id = ah.article
        where a.has_delete = 0 and ah.status = 'published'
        group by a.id
limit :limit 
offset :offset
    """
    )
    fun findAllArticle(limit:Int,offset: Int): Flux<QueryResultArticleDTO>

    @Query("""
        select count(a.id) from article a where a.has_delete = 0
    """)
    fun getCount():Mono<Int>

    fun existsByIdAndHasDeleteFalse(id: Int): Mono<Boolean>

    @Modifying
    @Query("""
        update article
        set has_delete = 1
        where id = :id
    """)
    fun logicDeleteAll(@Param("id") id: Int): Mono<Int>

    @Query("""
       select a.id as id,
       a.title,
       CONCAT('/',a.folder,'/v',ah.version,'.md') as markdown_url,
       CONCAT('/',a.folder,'/v',ah.version,'.fragment') as html_url,
       a.brief,
       a.author,
       a.created_at,
       ah.updated_at as updated_at,
       group_concat(c.id) as categories,
       group_concat(c.description) as category_types
from article a
         left join article_to_category atc on a.id = atc.article
         left join category c on atc.category = c.id
         left join article_history ah on a.id = ah.article
where a.has_delete = 0 and ah.status = 'published' and a.id = :id
group by a.id
    """)
    fun getArticleDetail(id: Int):Mono<QueryResultArticleDTO?>
}
