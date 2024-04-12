package per.misaka.misakanetworkscore

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.transaction.annotation.EnableTransactionManagement


@SpringBootApplication//(scanBasePackageClasses = [CustomExceptionHandler::class])
@EnableTransactionManagement
@EnableAsync
class MisakaNetworksCoreApplication

fun main(args: Array<String>) {
    runApplication<MisakaNetworksCoreApplication>(*args)
}
