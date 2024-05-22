package per.misaka.misakanetworkscore.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import per.misaka.misakanetworkscore.dto.STSDTO
import per.misaka.misakanetworkscore.service.FileService

@RestController
@RequestMapping("/internalApi/access")
class STSController(val ossService: FileService) {
    @GetMapping("/oss")
    suspend fun getOssToken(): STSDTO {
        return ossService.getSTS()
    }
}
