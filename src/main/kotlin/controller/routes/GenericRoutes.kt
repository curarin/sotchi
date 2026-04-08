package app.sotchi.controller.routes

import app.sotchi.controller.resources.Generic
import io.ktor.http.*
import io.ktor.server.plugins.ratelimit.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.genericRoutesV1() {
    rateLimit(RateLimitName("protected")) {
        /**
         * User retrieves all current available flour types.
         * Examples: wheat flour, rye flour, spelt flour,...
         */
        get<Generic.FlourType> {
            call.respondText(
                text = "These are all flour types we currently got.",
                status = HttpStatusCode.OK
            )
        }
    }
}

