package per.misaka.misakanetworkscore.repository

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import per.misaka.misakanetworkscore.entity.ImgEntity

interface ImgRepository : ReactiveCrudRepository<ImgEntity, Int> {
}
