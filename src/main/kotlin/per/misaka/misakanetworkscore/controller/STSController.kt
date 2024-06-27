package per.misaka.misakanetworkscore.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import per.misaka.misakanetworkscore.dto.STSDTO
import per.misaka.misakanetworkscore.service.FileService

@RestController
@RequestMapping("/internalApi/access")
class STSController {
    @Autowired
    lateinit var fileService: FileService
    @GetMapping("/sts")
    suspend fun getOssToken(): STSDTO {
        return fileService.getSTS()
    }
}
