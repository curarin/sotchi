package app.sotchi.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.defaultheaders.*

fun Application.configureDefaultHeader() {
    install(DefaultHeaders) {
        header(HttpHeaders.Server, "API")
        header(HttpHeaders.ContentType, "application/json; charset=utf-8")
        header(HttpHeaders.ContentDisposition, "inline; charset=utf-8")
        // Verhindert XSS-attacken > https://dev.to/sudiip__17/-important-http-response-headers-every-developer-should-know-4o2a
        header("Content-Security-Policy", "default-src 'self'")
        // Verhindert MIME-type sniffing attacken
        header("X-Content-Type-Options", "nosniff")
    }
}