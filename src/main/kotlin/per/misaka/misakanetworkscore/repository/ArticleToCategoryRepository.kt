package per.misaka.misakanetworkscore.repository

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import per.misaka.misakanetworkscore.entity.ArticleToCategoryEntity
import reactor.core.publisher.Mono
@Repository
interface ArticleToCategoryRepository: ReactiveCrudRepository<ArticleToCategoryEntity,Void> {
    @Query("""
        delete from article_to_category 
        where article_to_category.article=:articleId and article_to_category.category not in :category
    """)
    fun deleteNotUsed(@Param("article") articleId:Long,@Param("category") category:List<Int>):Mono<Any>

    fun deleteAllByArticle(articleId:Int):Mono<Void>
}