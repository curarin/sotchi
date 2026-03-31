package app.sotchi.plugins
import app.sotchi.repository.UserTable
import io.ktor.server.application.*
import org.jetbrains.exposed.v1.core.DatabaseConfig
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

fun Application.configureDatabases() {
    val database = Database.connect(
        url = "jdbc:h2:./dev",
        user = "root",
        driver = "org.h2.Driver",
        password = "root",
        databaseConfig = DatabaseConfig {
            defaultMaxAttempts = 3
        }
    )
    transaction {
        SchemaUtils.create(UserTable)
    }
}