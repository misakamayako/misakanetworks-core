package per.misaka.misakanetworkscore.repository

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import per.misaka.misakanetworkscore.entity.UserEntity
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface UserRepository : ReactiveCrudRepository<UserEntity, Int> {
    fun findByUsername(userName: String): Mono<UserEntity?>
    fun deleteByUsername(userName: String): Flux<Void>
    fun existsByUsername(userName: String): Flux<Boolean>
}
