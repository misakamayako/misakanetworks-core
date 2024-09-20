package per.misaka.misakanetworkscore.repository

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import per.misaka.misakanetworkscore.entity.FileMappingOfArticle
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
@Repository
interface FileMappingOfArticleRepository:ReactiveCrudRepository<FileMappingOfArticle,Int> {
    fun findAllByConnectFileAndDeleteFlagIsFalse(id:Int): Flux<FileMappingOfArticle>
    fun deleteAllByFileKeyIn(keys:List<String>): Mono<Int>
}