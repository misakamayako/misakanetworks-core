package per.misaka.misakanetworkscore.repository

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import per.misaka.misakanetworkscore.entity.DeleteConfirmEntity

interface DeleteConfirmRepository : ReactiveCrudRepository<DeleteConfirmEntity, Int> {
}
