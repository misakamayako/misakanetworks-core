package per.misaka.misakanetworkscore.exception


import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.FORBIDDEN)
class AuthoritiesException(message: String?) : RuntimeException(message)
