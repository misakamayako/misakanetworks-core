package per.misaka.misakanetworkscore.repository

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import per.misaka.misakanetworkscore.entity.AlbumEntity

interface AlbumRepository : ReactiveCrudRepository<AlbumEntity, Int>
