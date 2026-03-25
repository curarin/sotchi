package app.sotchi.controller.routes

import app.sotchi.controller.resources.UserAuth
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.Route


fun Route.userRoutesV1() {
    /**
     * Login for an existing user.
     */
    post<UserAuth.Login> {
        call.respondText { "You are trying to login!" }
    }

    /**
     * Modification for a user profile.
     */
    patch<UserAuth.Modify> {
        call.respondText { "You are trying to change some data!" }
    }

    /**
     * Deletes a users profile.
     */
    delete<UserAuth.Delete> {
        call.respondText { "You are trying to delete your account!" }
    }
}
