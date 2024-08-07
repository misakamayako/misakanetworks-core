package per.misaka.misakanetworkscore.repository

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import per.misaka.misakanetworkscore.entity.FileMappingOfArticle
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface FileMappingOfArticleRepository:ReactiveCrudRepository<FileMappingOfArticle,Int> {
    fun findAllByConnectFileAndDeleteFlagIsFalse(id:Int): Flux<FileMappingOfArticle>
    fun deleteAllByFileKeyIn(keys:List<String>): Mono<Int>
}