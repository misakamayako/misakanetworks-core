package per.misaka.misakanetworkscore.repository

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository
import per.misaka.misakanetworkscore.entity.ImgEntity
@Repository
interface ImgRepository : ReactiveCrudRepository<ImgEntity, Int> {
}
