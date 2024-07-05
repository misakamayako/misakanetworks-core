package per.misaka.misakanetworkscore.repository

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import per.misaka.misakanetworkscore.entity.ArticleToCategoryEntity

interface ArticleToCategoryRepository: ReactiveCrudRepository<ArticleToCategoryEntity,Void> {
}