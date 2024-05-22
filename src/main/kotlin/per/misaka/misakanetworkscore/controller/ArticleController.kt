package per.misaka.misakanetworkscore.controller

import jakarta.validation.Valid
import org.apache.logging.log4j.LogManager
import org.springframework.data.repository.query.Param
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import per.misaka.misakanetworkscore.dto.ArticleDTO
import per.misaka.misakanetworkscore.dto.ArticleUploadDTO
import per.misaka.misakanetworkscore.service.ArticleService

@RestController
@RequestMapping("/internalApi/article")
class ArticleController(private val articleService: ArticleService) {
    private val log = LogManager.getLogger(this::class.java)

    @PostMapping("")
    suspend fun createArticle(@Valid @RequestBody articleUploadDTO: ArticleUploadDTO): ResponseEntity<Void> {
        articleService.createArticle(articleUploadDTO)
        return ResponseEntity.noContent().build<Void>()
    }

    @PutMapping("{id}")
    suspend fun updateArticle(
        @Valid @RequestBody articleDTO: ArticleDTO,
        @Valid @Param("id") id: Int
    ): ResponseEntity<Void> {
        articleService.updateArticle(id, articleDTO)
        return ResponseEntity.noContent().build<Void>()
    }

    @DeleteMapping("{id}")
    suspend fun deleteArticle(@PathVariable("id") id: Int): ResponseEntity<Void> {
        return ResponseEntity.noContent().build<Void>()
    }
}
