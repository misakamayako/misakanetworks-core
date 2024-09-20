package per.misaka.misakanetworkscore.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.METHOD_NOT_ALLOWED)
class MethodNotAllowException(message: String?) : RuntimeException(message)