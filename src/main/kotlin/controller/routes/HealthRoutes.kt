package app.sotchi.controller.routes

import app.sotchi.controller.resources.Health
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.plugins.cachingheaders.*
import io.ktor.server.plugins.ratelimit.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.healthRouteV1() {
    rateLimit(RateLimitName("public")) {
        get<Health> {
            call.caching = CachingOptions(CacheControl.NoStore(visibility = CacheControl.Visibility.Public))
            call.respondText(
                "Alles in Ordnung!", status = HttpStatusCode.OK
            )
        }
    }
}