package per.misaka.misakanetworkscore.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.NOT_FOUND)
class NotFoundException(message: String = "we are sorry but what you want has gone") : RuntimeException(message)
