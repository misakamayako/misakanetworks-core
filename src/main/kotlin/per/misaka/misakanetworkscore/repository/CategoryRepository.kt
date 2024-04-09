package per.misaka.misakanetworkscore.repository

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import per.misaka.misakanetworkscore.entity.CategoryEntity

interface CategoryRepository : ReactiveCrudRepository<CategoryEntity, Int> {
    @Query("SELECT IF(COUNT(1) = :size, true, false) FROM category c WHERE c.id IN :ids")
    suspend fun allExistsByIds(@Param("ids") ids: List<Int>, @Param("size") size: Int): Boolean

}
