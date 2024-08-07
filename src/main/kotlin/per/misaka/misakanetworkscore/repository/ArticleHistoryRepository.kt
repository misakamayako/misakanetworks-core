package per.misaka.misakanetworkscore.repository

import org.springframework.data.repository.query.Param
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import per.misaka.misakanetworkscore.entity.ArticleHistory
import per.misaka.misakanetworkscore.entity.ArticleStatus
import reactor.core.publisher.Mono

interface ArticleHistoryRepository:ReactiveCrudRepository<ArticleHistory,Int> {
    fun findArticleHistoryByArticleAndStatusIs(article: Int, status: ArticleStatus): Mono<ArticleHistory>
}