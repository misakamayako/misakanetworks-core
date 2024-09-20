package per.misaka.misakanetworkscore.repository

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import per.misaka.misakanetworkscore.entity.ArticleHistory
import per.misaka.misakanetworkscore.entity.ArticleStatus
import reactor.core.publisher.Mono
@Repository
interface ArticleHistoryRepository:ReactiveCrudRepository<ArticleHistory,Int> {
    fun findArticleHistoryByArticleAndStatusIs(article: Int, status: ArticleStatus): Mono<ArticleHistory>
}