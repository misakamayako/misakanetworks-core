package per.misaka.misakanetworkscore.service

import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import per.misaka.misakanetworkscore.dto.AlbumBrief
import per.misaka.misakanetworkscore.dto.AlbumDTO
import per.misaka.misakanetworkscore.dto.AlbumDetail
import per.misaka.misakanetworkscore.dto.PageResultDTO
import per.misaka.misakanetworkscore.repository.AlbumRepository

@Service
class AlbumServiceImpl : AlbumService {
    private lateinit var albumRepository: AlbumRepository
    override suspend fun getAlbumBriefList(
        page: Int,
        pageSize: Int,
        isPrivate: Boolean?, name: String?, cec: Boolean?, categories: List<Int>?
    ): PageResultDTO<AlbumBrief?> {
        val pageable: Pageable = PageRequest.of(page - 1, pageSize)
        val list =
            albumRepository.findAlbumBrief(pageable, isPrivate, name, cec, categories).collectList()
        val count = albumRepository.getCount(isPrivate, name, cec, categories)
        return PageResultDTO(
            list.awaitSingleOrNull(),
            count.awaitSingle(),
            page,
            pageSize
        )
    }

    override suspend fun getAlbumDetail(id: Int): AlbumDetail {
        TODO("Not yet implemented")
    }

    override suspend fun createAlbum(album: AlbumDTO): AlbumDTO {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAlbum(id: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun updateAlbum(album: AlbumDTO): AlbumDTO {
        TODO("Not yet implemented")
    }

    override suspend fun checkIfExist(id: Int): Boolean {
        TODO("Not yet implemented")
    }
}