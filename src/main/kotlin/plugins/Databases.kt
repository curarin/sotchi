package app.sotchi.plugins

import app.sotchi.persistence.*
import io.ktor.server.application.*
import org.jetbrains.exposed.v1.core.DatabaseConfig
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

fun Application.configureDatabases() {
    val config = environment.config

    val url = config.property("ktor.database.url").getString()
    val user = config.property("ktor.database.user").getString()
    val password = config.property("ktor.database.password").getString()
    val driver = config.property("ktor.database.driver").getString()

    val database = Database.connect(
        url = url, user = user, driver = driver, password = password, databaseConfig = DatabaseConfig {
            defaultMaxAttempts = 3
        })
    transaction(database) {
        SchemaUtils.create(
            UserTable,
            UserRoleTable,
            UserActivationTable,
            SourdoughTable,
            FlourTable,
            LiquidTable,
            SourdoughFeedLogTable,
            SourdoughHealthStateTable
        )
    }
}