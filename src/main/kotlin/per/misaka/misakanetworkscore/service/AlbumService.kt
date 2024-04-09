package per.misaka.misakanetworkscore.service

import kotlinx.coroutines.*
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitLast
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import per.misaka.misakanetworkscore.dto.CreateAlbumDTO
import per.misaka.misakanetworkscore.dto.UpdateAlbumDTO
import per.misaka.misakanetworkscore.entity.AlbumEntity
import per.misaka.misakanetworkscore.entity.AlbumToCategoryEntity
import per.misaka.misakanetworkscore.exception.BadRequestException
import per.misaka.misakanetworkscore.exception.NotFoundException
import per.misaka.misakanetworkscore.repository.AlbumRepository
import per.misaka.misakanetworkscore.repository.AlbumToCategoryRepository
import per.misaka.misakanetworkscore.repository.CategoryRepository


@Service
class AlbumService(
    @Autowired
    private val albumDB: AlbumRepository,
    @Autowired
    private val categoryDB: CategoryRepository,
    @Autowired
    private val albumToCategoryDB: AlbumToCategoryRepository
) {
    @Throws(BadRequestException::class)
    @Transactional
    suspend fun createAlbum(createAlbumDTO: CreateAlbumDTO): AlbumEntity{
        if (createAlbumDTO.categories?.isNotEmpty() == true) {
            if (!categoryDB.allExistsByIds(createAlbumDTO.categories, createAlbumDTO.categories.size)) {
                throw BadRequestException("必选先添加所有的类型标签")
            }
        }
        val album: AlbumEntity = albumDB.save(
            AlbumEntity(
                title = createAlbumDTO.title,
                cover = createAlbumDTO.cover ?: "defaultImg",
                grading = createAlbumDTO.grading ?: 1,
                private = createAlbumDTO.private ?: false
            )
        ).awaitFirst()

        if (createAlbumDTO.categories?.isNotEmpty() == true) {
            albumToCategoryDB.saveAll(createAlbumDTO.categories.map {
                AlbumToCategoryEntity(
                    albumId = album.id!!,
                    categoryId = it
                )
            }).awaitLast()
        }
        return album
    }

    @Transactional
    suspend fun updateAlbum(updateAlbumDTO: UpdateAlbumDTO, id: Int): AlbumEntity = withContext(Dispatchers.IO) {
        if (albumDB.findById(id).awaitFirstOrNull() == null) {
            throw NotFoundException("没有这个相册")
        }
        if (updateAlbumDTO.categories != null) {
            albumToCategoryDB.deleteAlbumToCategoryEntitiesByAlbumId(id)
        }
        if (updateAlbumDTO.categories?.isNotEmpty() == true) {
            albumToCategoryDB.saveAll(updateAlbumDTO.categories.map {
                AlbumToCategoryEntity(
                    albumId = id,
                    categoryId = it
                )
            })
        }
        val updateEntity = AlbumEntity()
        updateEntity.id = id
        updateEntity.title = updateAlbumDTO.title
        updateEntity.grading = updateAlbumDTO.grading
        updateEntity.cover = updateAlbumDTO.cover
        updateEntity.private = updateAlbumDTO.private
        albumDB.save(updateEntity).awaitLast()
    }

    @Transactional
    suspend fun deleteAlbum(albumID: Int) {
        val album = albumDB.findById(albumID).awaitFirstOrNull() ?: throw NotFoundException("相册不存在")
        runBlocking { albumToCategoryDB.deleteAlbumToCategoryEntitiesByAlbumId(albumID) }
        albumDB.delete(album).awaitFirstOrNull()
    }
}
