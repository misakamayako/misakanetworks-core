package per.misaka.misakanetworkscore.controller

import jakarta.validation.Valid
import org.apache.logging.log4j.LogManager
import org.springframework.context.annotation.Bean
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import per.misaka.misakanetworkscore.dto.ArticleUploadDTO
import per.misaka.misakanetworkscore.entity.ArticleEntity
import per.misaka.misakanetworkscore.service.ArticleService

@RestController
@RequestMapping("/internalApi/article")
class ArticleController(private val service: ArticleService) {
    companion object {
        @JvmStatic
        private val log = LogManager.getLogger(this::class.java)
    }

    @PostMapping("")
    suspend fun getArticle(@Valid @RequestBody articleUploadDTO: ArticleUploadDTO): ArticleEntity {
        return service.createArticle(articleUploadDTO)
    }
}
