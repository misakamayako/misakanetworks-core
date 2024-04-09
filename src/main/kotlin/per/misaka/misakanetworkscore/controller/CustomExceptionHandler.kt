package per.misaka.misakanetworkscore.controller

import jakarta.annotation.Priority
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import per.misaka.misakanetworkscore.exception.UnauthorizedException
import per.misaka.misakanetworkscore.exception.unofficialError.UnofficialError
import java.lang.reflect.Method



@ControllerAdvice(annotations = [RestController::class])
@Priority(1)
class CustomExceptionHandler {
    @ExceptionHandler(UnofficialError::class)
    fun handleCustomException(ex: UnofficialError): ResponseEntity<String> {
        println(ex.message)
        return ResponseEntity.status(ex.statusCode).body(ex.message)
    }

    @ExceptionHandler(UnauthorizedException::class)
    fun handleDefaultException(ex: UnauthorizedException): ResponseEntity<String> {
        val code = getAnnotationStatusCode(ex::class.java) ?: 500
        return ResponseEntity.status(code).body(ex.message)
    }

    fun getAnnotationStatusCode(exceptionClass: Class<out Throwable>): Int? {
        val annotation = exceptionClass.getAnnotation(ResponseStatus::class.java)
        return annotation?.let {
            val valueMethod: Method = annotation.javaClass.getMethod("value")
            val status: HttpStatus = valueMethod.invoke(annotation) as HttpStatus
            status.value()
        }
    }
}
