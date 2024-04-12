package per.misaka.misakanetworkscore.controller

import jakarta.annotation.Priority
import org.slf4j.LoggerFactory.getLogger
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import per.misaka.misakanetworkscore.dto.ErrorResponseDto
import per.misaka.misakanetworkscore.exception.unofficialError.UnofficialError
import java.lang.reflect.Method


@ControllerAdvice(annotations = [RestController::class])
@Priority(1)
class CustomExceptionHandler {
    private val logger = getLogger(CustomExceptionHandler::class.java)

    @ExceptionHandler(UnofficialError::class)
    fun handleCustomException(ex: UnofficialError): ResponseEntity<String> {
        return ResponseEntity.status(ex.statusCode).body(ex.message)
    }

    @ExceptionHandler(RuntimeException::class)
    fun handleDefaultException(ex: RuntimeException): ResponseEntity<String> {
        val code = getAnnotationStatusCode(ex::class.java) ?: 500
        return ResponseEntity.status(code).body(ex.message)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleDefaultException(ex: HttpMessageNotReadableException): ResponseEntity<String> {
        val code = 400
        val message = "input format error"
        logger.info(ex.message)
        return ResponseEntity.status(code).body(message)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleParseJSONError(ex: MethodArgumentNotValidException):ResponseEntity<ErrorResponseDto> {
        val errors = ex.bindingResult.allErrors.joinToString(",") { it.defaultMessage.toString() }
        val code = 400
        val errorResponseDto = ErrorResponseDto(errors, code)
        return ResponseEntity.status(code).body(errorResponseDto)
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
