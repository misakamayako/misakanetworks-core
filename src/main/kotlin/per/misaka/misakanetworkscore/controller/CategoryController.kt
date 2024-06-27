package per.misaka.misakanetworkscore.controller

import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import per.misaka.misakanetworkscore.dto.CategoryDTO
import per.misaka.misakanetworkscore.dto.CategoryType
import per.misaka.misakanetworkscore.service.CategoryService

@RestController
@RequestMapping("/internalApi/category")
class CategoryController(private val service: CategoryService) {

    @PostMapping("")
    suspend fun createCategory(@RequestBody @Validated category: CategoryDTO): CategoryDTO {
        return service.createCategory(category)
    }

    @GetMapping("")
    suspend fun getAllCateGory(
        @RequestParam(name = "type", required = false)
        categoryType: CategoryType
    ): List<CategoryDTO> {
        return service.getAllCategory(categoryType)
    }
}
