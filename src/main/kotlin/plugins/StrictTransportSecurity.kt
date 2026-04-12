package app.sotchi.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.hsts.*

/**
 * Only works for Browser Clients, not app or smth. But since we might publish to web as well, this is implemented
 */
fun Application.configureStrictTransportSecurity() {
    install(HSTS) {
        maxAgeInSeconds = 31536000 // 1 Jahr > solange soll der Browser sich merken, dass wir nur über HTTPS aufgerufen werden wollen
        includeSubDomains = false
    }
}