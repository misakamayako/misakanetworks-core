package per.misaka.misakanetworkscore.repository.withDynamic

import org.springframework.data.domain.Pageable
import per.misaka.misakanetworkscore.dto.ArticleBrief
import reactor.core.publisher.Flux

interface WithDynamicArticleR2dbc {
    fun findArticleByQuery(pageable: Pageable, title: String?, category: List<Int>?): Flux<ArticleBrief>
}
