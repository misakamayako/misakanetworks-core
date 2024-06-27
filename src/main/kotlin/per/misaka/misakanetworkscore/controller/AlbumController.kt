package per.misaka.misakanetworkscore.controller

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import per.misaka.misakanetworkscore.dto.CreateAlbumDTO
import per.misaka.misakanetworkscore.dto.UpdateAlbumDTO
import per.misaka.misakanetworkscore.service.AlbumService
import java.net.URI

@RestController
@RequestMapping("/internalApi/album")
class AlbumController {
    @Autowired
    private lateinit var service: AlbumService
    val log: Log = LogFactory.getLog(AlbumController::class.java)

    @PostMapping("")
    suspend fun createAlbum(@RequestBody @Validated createAlbumDTO: CreateAlbumDTO): ResponseEntity<Void> {
        log.info("create album with $createAlbumDTO")
        val id = service.createAlbum(createAlbumDTO).id
        return ResponseEntity.created(URI.create("/album/$id")).build()
    }

    @DeleteMapping("/{id}")
    suspend fun deleteAlbumById(@PathVariable id: Int): ResponseEntity<Void> {
        service.deleteAlbum(id)
        return ResponseEntity.noContent().build()
    }

    @PutMapping("/{id}")
    suspend fun updateAlbumById(@RequestBody data: UpdateAlbumDTO, @PathVariable id: Int): ResponseEntity<Void> {
        service.updateAlbum(data, id)
        return ResponseEntity.accepted().build()
    }
}
