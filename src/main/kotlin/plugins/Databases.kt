package app.sotchi.plugins
import org.jetbrains.exposed.v1.jdbc.Database

fun configureDatabases() {
    val database = Database.connect(
        url = "jdbc:h2:mem:test",
        user = "root",
        driver = "org.h2.Driver",
        password = "",
    )
}