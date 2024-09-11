package per.misaka.misakanetworkscore.repository

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import per.misaka.misakanetworkscore.entity.CategoryEntity
import per.misaka.misakanetworkscore.enums.CategoryType
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface CategoryRepository : ReactiveCrudRepository<CategoryEntity, Int> {
    @Query("SELECT IF(COUNT(1) = :size, true, false) FROM category c WHERE c.id IN :ids")
    fun allExistsByIds(@Param("ids") ids: List<Int>, @Param("size") size: Int): Mono<Boolean>

    @Query("SELECT * FROM category WHERE type = :type")
    fun findAllByType(@Param("type") type:CategoryType): Flux<CategoryEntity>
}
