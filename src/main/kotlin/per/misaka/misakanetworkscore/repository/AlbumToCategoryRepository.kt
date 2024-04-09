package per.misaka.misakanetworkscore.repository

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import per.misaka.misakanetworkscore.entity.AlbumToCategoryEntity


interface AlbumToCategoryRepository : ReactiveCrudRepository<AlbumToCategoryEntity, Void> {
    suspend fun deleteAlbumToCategoryEntitiesByAlbumId(id: Int)
}
