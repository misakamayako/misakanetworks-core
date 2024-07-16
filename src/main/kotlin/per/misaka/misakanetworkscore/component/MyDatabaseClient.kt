package per.misaka.misakanetworkscore.component

import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration
import org.springframework.r2dbc.connection.R2dbcTransactionManager
import org.springframework.stereotype.Component
import org.springframework.transaction.ReactiveTransactionManager


@Component
final class MyDatabaseClient : AbstractR2dbcConfiguration() {
    @Value("\${spring.r2dbc.url}")
    private lateinit var dataBaseUrl: String
    override fun connectionFactory(): ConnectionFactory {
        return ConnectionFactories.get(dataBaseUrl)
    }

    @Bean
    fun transactionManager(connectionFactory: ConnectionFactory?): ReactiveTransactionManager {
        return R2dbcTransactionManager(connectionFactory!!)
    }
}