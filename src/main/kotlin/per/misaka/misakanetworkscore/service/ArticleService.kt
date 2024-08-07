package per.misaka.misakanetworkscore.service

import per.misaka.misakanetworkscore.dto.ArticleDTO
import per.misaka.misakanetworkscore.dto.PageResultDTO
import per.misaka.misakanetworkscore.dto.QueryResultArticleDTO

interface ArticleService {
    suspend fun checkIfExists(id:Int):Boolean
    fun renderMarkdownToHtml(content:String,replaceOrigin:String?):Pair<String,HashMap<String,String>?>
    fun renderMarkdownToHtml(content:String):String
    suspend fun createArticle(articleDTO: ArticleDTO):ArticleDTO
    suspend fun updateArticle(articleDTO: ArticleDTO):Unit
    suspend fun deleteArticle(articleId:Int):Unit
    suspend fun queryArticleList(page:Int,pageSize:Int): PageResultDTO<QueryResultArticleDTO?>
    suspend fun getArticleDetail(id:Int): QueryResultArticleDTO?
    suspend fun getImagesOfArticle(articleID:Int):List<String>
}