package per.misaka.misakanetworkscore.controller

import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import per.misaka.misakanetworkscore.dto.CategoryDTO
import per.misaka.misakanetworkscore.service.CategoryService

@RestController
@RequestMapping("/internalApi/category")
class CategoryController(private val service: CategoryService) {

    @PostMapping("")
    suspend fun createCategory(@RequestBody @Valid category: CategoryDTO): CategoryDTO {
        return service.createCategory(category)
    }
}
