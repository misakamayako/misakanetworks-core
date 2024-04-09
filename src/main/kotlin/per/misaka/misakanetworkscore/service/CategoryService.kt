package per.misaka.misakanetworkscore.service

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Service
import per.misaka.misakanetworkscore.dto.CategoryDTO
import per.misaka.misakanetworkscore.entity.CategoryEntity
import per.misaka.misakanetworkscore.repository.CategoryRepository


@Service
class CategoryService(private val db: CategoryRepository) {
    suspend fun createCategory(categoryDTO: CategoryDTO): CategoryDTO {
        return db
            .save(CategoryEntity(type = categoryDTO.type ?: 1, description = categoryDTO.category))
            .awaitSingle()
            .let {
                CategoryDTO(id = it.id, category = it.description, type = it.type)
            }
    }
}
