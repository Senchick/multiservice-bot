package io.microservicebot.core

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv
import org.jetbrains.exposed.sql.Database

suspend fun init(block: suspend Configuration.() -> Unit) = block(Configuration())

class Configuration {
    val env = SafeDotenv(dotenv())

    init {
        Database.connect(
            HikariDataSource(HikariConfig().apply {
                jdbcUrl  = env["JDBC_URL"]
                username = env["DB_USERNAME"]
                password = env["DB_PASSWORD"]
                maximumPoolSize = env["DB_POOL_SIZE"].toInt()
            })
        )
    }
}


class SafeDotenv(private val env: Dotenv) {
    operator fun get(key: String) = env[key] ?: throw NotFoundException("$key key not found.")
}

class NotFoundException(message: String): Exception(message)