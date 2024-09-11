package per.misaka.misakanetworkscore.controller

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import per.misaka.misakanetworkscore.dto.AlbumBrief
import per.misaka.misakanetworkscore.dto.AlbumDTO
import per.misaka.misakanetworkscore.dto.PageResultDTO
import per.misaka.misakanetworkscore.exception.BadRequestException
import per.misaka.misakanetworkscore.service.AlbumService
import java.net.URI

@RestController
@RequestMapping("/internalApi/album")
class AlbumController {
    @Autowired
    private lateinit var albumService: AlbumService

    val log: Log = LogFactory.getLog(AlbumController::class.java)

    @PostMapping("")
    suspend fun createAlbum(@RequestBody @Validated albumDTO: AlbumDTO): ResponseEntity<Void> {
        val id = albumService.createAlbum(albumDTO)
        return ResponseEntity.created(URI.create("/album/$id")).build()
    }

    @GetMapping("")
    suspend fun getAlbumBrief(
        @RequestParam(value = "page", required = false) page: Int = 1,
        @RequestParam(value = "pageSize", required = false) pageSize: Int = 10,
        @RequestParam(value = "isPrivate", required = false) isPrivate: Boolean?,
        @RequestParam(value = "name", required = false) name: String?,
        @RequestParam(value = "cec", required = false) cec: Boolean?,
        @RequestParam(value = "categories", required = false) categories: List<Int>?,
    ): PageResultDTO<AlbumBrief?> {
        return albumService.getAlbumBriefList(page,pageSize,isPrivate,name,cec,categories)
    }

    @DeleteMapping("/{id}")
    suspend fun deleteAlbumById(@PathVariable id: Int): ResponseEntity<Void> {
        albumService.deleteAlbum(id)
        return ResponseEntity.noContent().build()
    }

    @PutMapping("/{id}")
    suspend fun updateAlbumById(@RequestBody data: AlbumDTO, @PathVariable id: Int): ResponseEntity<Void> {
        if (data.id!=id) throw BadRequestException("请求参数与路径不符")
        albumService.updateAlbum(data)
        return ResponseEntity.accepted().build()
    }
}
