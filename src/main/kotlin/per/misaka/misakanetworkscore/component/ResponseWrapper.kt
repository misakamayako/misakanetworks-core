package per.misaka.misakanetworkscore.component

import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import per.misaka.misakanetworkscore.annotation.NoReturnLog
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
        val methodSignature = joinPoint.signature as MethodSignature
        val method = methodSignature.method
        val showLog = !method.isAnnotationPresent(NoReturnLog::class.java)
        val result: Any? = joinPoint.proceed()
        return mono {
            val wrappedResponse = if (result is Mono<*>) {
                val data = result.awaitSingleOrNull()
                if (data is ResponseEntity<*>) {
                    data
                } else {
                    ApiResponse(
                        code = 200,
                        message = "Success",
                        data = data
                    )
                }
            } else {
                ApiResponse(
                    code = 200,
                    message = "Success",
                    data = result
                )
            }

            if (showLog) {
                logger.info("Method {} returned response: {}", method.name, wrappedResponse)
            }
            wrappedResponse
        }.onErrorResume { ex ->
            logger.error("Error occurred in method {}: {}", method.name, ex.message, ex)
            throw ex
        }
    }
}
