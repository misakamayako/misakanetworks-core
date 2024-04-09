package per.misaka.misakanetworkscore.repository

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import per.misaka.misakanetworkscore.entity.ArticleEntity

interface ArticleRepository : ReactiveCrudRepository<ArticleEntity, Int> {
    suspend fun findByTitle(title: String): ArticleEntity?
}
