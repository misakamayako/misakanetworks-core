package per.misaka.misakanetworkscore.service

import per.misaka.misakanetworkscore.dto.AlbumBrief
import per.misaka.misakanetworkscore.dto.AlbumDTO
import per.misaka.misakanetworkscore.dto.AlbumDetail
import per.misaka.misakanetworkscore.dto.PageResultDTO


interface AlbumService{
    suspend fun getAlbumBriefList(page:Int,pageSize:Int,isPrivate:Boolean?,name:String?,cec:Boolean?,categories:List<Int>?): PageResultDTO<AlbumBrief?>
    suspend fun getAlbumDetail(id:Int): AlbumDetail
    suspend fun createAlbum(album:AlbumDTO):AlbumDTO
    suspend fun deleteAlbum(id:Int)
    suspend fun updateAlbum(album:AlbumDTO):AlbumDTO
    suspend fun checkIfExist(id:Int):Boolean
}

