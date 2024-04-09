package per.misaka.misakanetworkscore

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.transaction.annotation.EnableTransactionManagement


@SpringBootApplication//(scanBasePackageClasses = [CustomExceptionHandler::class])
@EnableTransactionManagement
class MisakaNetworksCoreApplication

fun main(args: Array<String>) {
    runApplication<MisakaNetworksCoreApplication>(*args)
}
