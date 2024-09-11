package per.misaka.misakanetworkscore.service

import per.misaka.misakanetworkscore.dto.ArticleBrief
import per.misaka.misakanetworkscore.dto.ArticleDTO
import per.misaka.misakanetworkscore.dto.PageResultDTO
import per.misaka.misakanetworkscore.dto.ArticleDetailDTO
import java.util.HashMap

interface ArticleService {
    suspend fun checkIfExists(id:Int):Boolean
    fun renderMarkdownToHtml(content:String,replaceOrigin:String?): Pair<String, HashMap<*, *>?>
    fun renderMarkdownToHtml(content:String):String
    suspend fun createArticle(articleDTO: ArticleDTO):ArticleDTO
    suspend fun updateArticle(articleDTO: ArticleDTO):Unit
    suspend fun deleteArticle(articleId:Int):Unit
    suspend fun queryArticleList(page:Int,pageSize:Int,title:String?,categories:List<Int>?): PageResultDTO<ArticleBrief?>
    suspend fun getArticleDetail(id:Int): ArticleDetailDTO?
    suspend fun getImagesOfArticle(articleID:Int):List<String>
}