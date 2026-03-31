package app.sotchi.plugins
import app.sotchi.repository.UserTable
import org.jetbrains.exposed.v1.core.DatabaseConfig
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.JdbcTransactionManager
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.sql.Connection

fun configureDatabases() {
    val database = Database.connect(
        url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
        user = "root",
        driver = "org.h2.Driver",
        password = "",
        //transactionIsolation = Connection.TRANSACTION_READ_COMMITTED
        databaseConfig = DatabaseConfig {
            defaultMaxAttempts = 3
        }
    )
    transaction {
        SchemaUtils.create(UserTable)
    }
}