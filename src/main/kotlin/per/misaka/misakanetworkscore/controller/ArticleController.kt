package per.misaka.misakanetworkscore.controller

import jakarta.validation.Valid
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import per.misaka.misakanetworkscore.dto.*
import per.misaka.misakanetworkscore.service.ArticleService

@RestController
@RequestMapping("/internalApi/article")
class ArticleController {
    private val log = LogManager.getLogger(this::class.java)

    @Autowired
    private lateinit var articleService: ArticleService


    @PostMapping("")
    suspend fun createArticle(@Valid @RequestBody articleUploadDTO: ArticleUploadDTO): ResponseEntity<Void> {
        articleService.createArticle(articleUploadDTO)
        return ResponseEntity.noContent().build<Void>()
    }

    @GetMapping("")
    suspend fun queryArticle(
        @RequestParam(value = "page", required = false) page: Int = 1,
        @RequestParam(value = "pageSize", required = false) pageSize: Int = 10
    ): PageResultDTO<QueryResultArticleDTO> {
        return articleService.getArticleList(page, pageSize)
    }

    @GetMapping("{id}")
    suspend fun getArticleDetail(
        @Valid @PathVariable("id") id:Long
    ): ArticleDetailDTO? {
        return  articleService.getArticleDetail(id)
    }

    @PutMapping("{id}")
    suspend fun updateArticle(
        @Valid @RequestBody articleDTO: ArticleDTO,
        @Valid @PathVariable("id") id: Int
    ): ResponseEntity<Void> {
        articleService.updateArticle(id, articleDTO)
        return ResponseEntity.noContent().build<Void>()
    }

    @DeleteMapping("{id}")
    suspend fun deleteArticle(@PathVariable("id") id: Long): ResponseEntity<Void> {
        articleService.deleteArticle(id)
        return ResponseEntity.noContent().build<Void>()
    }

    @PostMapping("preview")
    suspend fun getPreview(@RequestBody articlePreviewContent: ArticlePreviewContent): String {
        return articleService.preview(articlePreviewContent.content)
    }
}
