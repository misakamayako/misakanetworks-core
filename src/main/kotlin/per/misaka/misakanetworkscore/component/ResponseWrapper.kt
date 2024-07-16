package per.misaka.misakanetworkscore.component

import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import per.misaka.misakanetworkscore.dto.ApiResponse
import reactor.core.publisher.Mono

@Aspect
@Component
class ResponseWrapperAspect {
    val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    fun controllerMethods() {
    }

    @Around("controllerMethods()")
    fun wrapResponse(joinPoint: ProceedingJoinPoint): Mono<Any?> {
        return mono {
            val result: Any? = joinPoint.proceed()
            if (result is Mono<*>?) {
                val data: Any? = result?.awaitSingleOrNull()
                if (data is ResponseEntity<*>) {
                    data
                } else {
                    logger.warn("Response body is not a ResponseEntity: ${data.toString()}")
                    ApiResponse(
                        code = 200,
                        message = "Success",
                        data = data
                    )
                }
            } else {
                logger.warn("Response body type is unknown: ${result.toString()}")
                ApiResponse(
                    code = 200,
                    message = "Success",
                    data = result
                )
            }

        }.onErrorResume { ex ->
            logger.error("find Error ${ex.message}",ex)
            throw ex
        }
    }
}
