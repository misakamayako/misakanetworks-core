package per.misaka.misakanetworkscore.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import per.misaka.misakanetworkscore.dto.ImgUploadDTO
import per.misaka.misakanetworkscore.entity.ImgEntity
import per.misaka.misakanetworkscore.entity.ImgToCategoryEntity
import per.misaka.misakanetworkscore.exception.BadRequestException
import per.misaka.misakanetworkscore.exception.NotFoundException
import per.misaka.misakanetworkscore.repository.CategoryRepository
import per.misaka.misakanetworkscore.repository.ImgRepository
import per.misaka.misakanetworkscore.repository.ImgToCategoryRepository

@Service
class ImgService(
    private val imgDb: ImgRepository,
    private val categoryRepository: CategoryRepository,
    private val imgToCategoryRepository: ImgToCategoryRepository
) {
    @Transactional
    suspend fun createImgRecord(imgUploadDTO: ImgUploadDTO): ImgEntity = withContext(Dispatchers.IO) {
        if (imgUploadDTO.categories?.isNotEmpty() == true) {
            if (!categoryRepository.allExistsByIds(imgUploadDTO.categories, imgUploadDTO.categories.size)) {
                throw BadRequestException("请先创建所有的标签")
            }
        }
        val result = imgDb.save(
            ImgEntity(
                eigenvalues = imgUploadDTO.fileUrl.substringAfterLast('/').substringBeforeLast('.'),
                name = imgUploadDTO.name,
                grading = imgUploadDTO.grading,
                private = imgUploadDTO.private,
                album = imgUploadDTO.album
            )
        ).awaitSingle()
        if (imgUploadDTO.categories?.isNotEmpty() == true) {
            imgToCategoryRepository.saveAll(imgUploadDTO.categories.map {
                ImgToCategoryEntity(
                    imgId = it,
                    categoryId = result.id!!
                )
            })
        }
        result
    }

    @Transactional
    suspend fun deleteImg(imgId: Int) = withContext(Dispatchers.IO) {
        if (imgDb.findById(imgId).awaitFirstOrNull()==null) {
            throw NotFoundException()
        }
        val connections = imgToCategoryRepository.findAllByImgId(imgId)
        if (connections.isNotEmpty()) {
            imgToCategoryRepository.deleteAllById(connections.map { it.id })
        }
        imgDb.deleteById(imgId)
    }
}
