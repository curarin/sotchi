package app.sotchi.controller.routes

import app.sotchi.controller.resources.Health
import io.ktor.http.HttpStatusCode
import io.ktor.server.resources.get
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route

fun Route.healthRouteV1() {
    get<Health> {
        call.respondText(
            "Alles in Ordnung!",
            status = HttpStatusCode.OK
        )
    }
}