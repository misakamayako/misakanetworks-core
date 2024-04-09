package per.misaka.misakanetworkscore.controller

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import per.misaka.misakanetworkscore.dto.ImgUploadDTO
import per.misaka.misakanetworkscore.service.ImgService
import java.net.URI

@RestController
@RequestMapping("/internalApi/img")
class ImgController(private val service: ImgService) {
    @PostMapping("")
    suspend fun createImgRecord(@RequestBody @Valid imgUploadDTO: ImgUploadDTO): ResponseEntity<Void> {
        val result = service.createImgRecord(imgUploadDTO)
        return ResponseEntity.created(URI.create("/img/${result.id!!}")).build()
    }

    @DeleteMapping("{id}")
    suspend fun deleteImg(@PathVariable id: Int): ResponseEntity<Void> {
        service.deleteImg(id)
        return ResponseEntity.noContent().build()
    }
}
