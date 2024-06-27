package per.misaka.misakanetworkscore.service

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Service
import per.misaka.misakanetworkscore.dto.CategoryDTO
import per.misaka.misakanetworkscore.dto.CategoryType
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
    suspend fun getAllCategory(type: CategoryType):List<CategoryDTO>{
        return db.findAllByType(type.type!!).map { CategoryDTO(it.description,it.id,it.type) }
    }
}
