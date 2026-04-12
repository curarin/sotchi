package controller.routes

import app.sotchi.module
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.config.*
import io.ktor.server.testing.*

/**
 * Sets H2 as in-mem database up so we can do some integration tests.
 */
fun withTestApplication(
    block: suspend ApplicationTestBuilder.(HttpClient) -> Unit
) = testApplication {
    environment {
        config = MapApplicationConfig(
            "ktor.jwt.secret" to "test-secret",
            "ktor.jwt.issuer" to "test-issuer",
            "ktor.jwt.audience" to "test-audience",
            "ktor.jwt.realm" to "test-realm",
            "ktor.database.url" to "jdbc:h2:mem:test_${System.nanoTime()};DB_CLOSE_DELAY=-1",
            "ktor.database.user" to "sa",
            "ktor.database.password" to "",
            "ktor.database.driver" to "org.h2.Driver"
        )
    }
    application {
        module()
    }

    val client = createClient {
        install(ContentNegotiation) {
            json()
        }
    }

    block(client)
}