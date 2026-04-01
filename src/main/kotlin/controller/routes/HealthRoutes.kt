package app.sotchi.controller.routes

import app.sotchi.controller.resources.Health
import io.ktor.http.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.healthRouteV1() {
    get<Health> {
        call.respondText(
            "Alles in Ordnung!",
            status = HttpStatusCode.OK
        )
    }
}