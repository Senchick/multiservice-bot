package io.multiservicebot.core

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.Database

suspend fun init(block: suspend Configuration.() -> Unit) = block(Configuration())

class Configuration {
    val env = SafeDotenv(dotenv())
    val client = HttpClient(CIO) {
        val json = Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
        }

        install(ContentNegotiation) {
            json(json)
        }

        install(Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.HEADERS
        }

        install(HttpTimeout) {
            requestTimeoutMillis = HttpTimeout.INFINITE_TIMEOUT_MS
            connectTimeoutMillis = HttpTimeout.INFINITE_TIMEOUT_MS
            socketTimeoutMillis = HttpTimeout.INFINITE_TIMEOUT_MS
        }
    }

    init {
        Database.connect(
            HikariDataSource(HikariConfig().apply {
                jdbcUrl = env["JDBC_URL"]
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

class NotFoundException(message: String) : Exception(message)