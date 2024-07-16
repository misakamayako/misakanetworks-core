package per.misaka.misakanetworkscore.repository

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import per.misaka.misakanetworkscore.entity.FileMappingOfArticle
import reactor.core.publisher.Flux

interface FileMappingOfArticleRepository:ReactiveCrudRepository<FileMappingOfArticle,Long> {
    fun findAllByConnectFile(id:Long): Flux<FileMappingOfArticle>
}