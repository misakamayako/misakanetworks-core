package per.misaka.misakanetworkscore.repository

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository
import per.misaka.misakanetworkscore.dto.AlbumBrief
import per.misaka.misakanetworkscore.entity.AlbumEntity
import per.misaka.misakanetworkscore.repository.withDynamic.WithDynamicAlbumRepository
import reactor.core.publisher.Flux

@Repository
interface AlbumRepositoryBase : ReactiveCrudRepository<AlbumEntity, Int> {
    @Query(
        """
        select a.*, i.oss_key as coverURL
from albums a
         left join images i on i.id = a.cover_id
        limit :limit offset :offset
    """
    )
    fun getAlbumList(limit: Int, offset: Int): Flux<AlbumBrief>
}

@Repository
interface AlbumRepository : AlbumRepositoryBase, WithDynamicAlbumRepository