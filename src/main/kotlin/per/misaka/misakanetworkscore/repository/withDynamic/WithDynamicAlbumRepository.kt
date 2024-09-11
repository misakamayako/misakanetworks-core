package per.misaka.misakanetworkscore.repository.withDynamic

import org.springframework.data.domain.Pageable
import per.misaka.misakanetworkscore.dto.AlbumBrief
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface WithDynamicAlbumRepository {
    fun findAlbumBrief(pageable: Pageable,isPrivate:Boolean?,name:String?,cec:Boolean?,categories:List<Int>?): Flux<AlbumBrief>

    fun getCount(isPrivate:Boolean?,name:String?,cec:Boolean?,categories:List<Int>?): Mono<Int>
}