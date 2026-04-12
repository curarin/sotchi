package app.sotchi

import app.sotchi.plugins.*
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureCors()
    configureStrictTransportSecurity()
    configureDefaultHeader()
    configureRatelimit()
    configureCaching()
    configureAuthentication()
    configureResources()
    configureDatabases()
    configureStatusPages()
    configureRouting()
    configureSerialization()
}