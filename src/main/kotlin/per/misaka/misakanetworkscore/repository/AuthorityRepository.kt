package per.misaka.misakanetworkscore.repository

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import per.misaka.misakanetworkscore.entity.AuthoritiesEntity


interface AuthorityRepository : ReactiveCrudRepository<AuthoritiesEntity, Void> {
    suspend fun getAllByUserId(userId: Int): List<AuthoritiesEntity>
}
