package app.sotchi.controller.routes

import app.sotchi.controller.resources.Generic
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respondText
import io.ktor.server.resources.get
import io.ktor.server.routing.Route

fun Route.genericRoutesV1() {
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

