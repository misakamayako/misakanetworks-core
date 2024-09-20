package per.misaka.misakanetworkscore.repository

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository
import per.misaka.misakanetworkscore.entity.ImgToCategoryEntity
@Repository
interface ImgToCategoryRepository : ReactiveCrudRepository<ImgToCategoryEntity, Int> {
    suspend fun findAllByImgId(ids: Int): List<ImgToCategoryEntity>
}
