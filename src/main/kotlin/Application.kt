package app.sotchi

import app.sotchi.plugins.configureResources
import app.sotchi.plugins.configureRouting
import app.sotchi.plugins.configureSerialization
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureResources()
    configureRouting()
    configureSerialization()
}