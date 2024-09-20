package per.misaka.misakanetworkscore.repository

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository
import per.misaka.misakanetworkscore.entity.AuthoritiesEntity

@Repository
interface AuthorityRepository : ReactiveCrudRepository<AuthoritiesEntity, Void> {
    suspend fun getAllByUserId(userId: Int): List<AuthoritiesEntity>
}
