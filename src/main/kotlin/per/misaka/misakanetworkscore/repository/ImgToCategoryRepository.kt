package per.misaka.misakanetworkscore.repository

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import per.misaka.misakanetworkscore.entity.ImgToCategoryEntity

interface ImgToCategoryRepository : ReactiveCrudRepository<ImgToCategoryEntity, Int> {
    suspend fun findAllByImgId(ids: Int): List<ImgToCategoryEntity>
}
