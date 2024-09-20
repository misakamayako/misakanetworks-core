package per.misaka.misakanetworkscore.repository

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import per.misaka.misakanetworkscore.entity.AlbumToCategoryEntity

@Repository
interface AlbumToCategoryRepository : ReactiveCrudRepository<AlbumToCategoryEntity, Void> {
    suspend fun deleteAlbumToCategoryEntitiesByAlbumId(id: Int)
}
