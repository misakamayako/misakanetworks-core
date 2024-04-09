package per.misaka.misakanetworkscore.repository

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import per.misaka.misakanetworkscore.entity.UserEntity

interface UserRepository : ReactiveCrudRepository<UserEntity, Int> {
    suspend fun findByUsername(userName: String): UserEntity?
    suspend fun deleteByUsername(userName: String)
    suspend fun existsByUsername(userName: String): Boolean
}
