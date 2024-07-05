package per.misaka.misakanetworkscore.component

import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.mono
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import per.misaka.misakanetworkscore.dto.ApiResponse
import reactor.core.publisher.Mono

@Aspect
@Component
class ResponseWrapperAspect {
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    fun controllerMethods() {}

    @Around("controllerMethods()")
    fun wrapResponse(joinPoint: ProceedingJoinPoint): Mono<Any?> {
        return mono {
            val result = joinPoint.proceed() as Mono<*>?
            val data = result?.awaitSingle()
            if (data is ResponseEntity<*>) {
                data
            } else {
                ApiResponse(
                    code = 200,
                    message = "Success",
                    data = data
                )
            }
        }
    }
}
