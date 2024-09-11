package per.misaka.misakanetworkscore.repository

import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import per.misaka.misakanetworkscore.dto.ArticleDetailDTO
import per.misaka.misakanetworkscore.entity.ArticleEntity
import per.misaka.misakanetworkscore.repository.withDynamic.WithDynamicArticleR2dbc
import reactor.core.publisher.Mono

@Repository
interface BaseArticleRepository : ReactiveCrudRepository<ArticleEntity, Int> {

    @Query("""
        select count(a.id) from article a where a.has_delete = 0
    """)
    fun getCount():Mono<Int>

    @Query("""
        select count(a.id) from article a where a.has_delete = 0 and title like :title
    """)
    fun getCount(title:String):Mono<Int>

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
    fun getArticleDetail(id: Int):Mono<ArticleDetailDTO?>

    @Modifying
    @Query("""
insert into article(id,views,title, author, folder)
    values
    (:id,:increase,'','','')
on duplicate key update views = views + VALUES(views);
    """)
    fun increaseViews(@Param("id") id:Int,@Param("increase") increase:Int):Mono<Int>
}

interface ArticleRepository: BaseArticleRepository,WithDynamicArticleR2dbc