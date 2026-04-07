package app.sotchi.controller.routes

import app.sotchi.controller.resources.Sourdough
import app.sotchi.dto.sourdough.CreateSourdoughDTO
import app.sotchi.dto.sourdough.DeleteSourdoughDTO
import app.sotchi.service.SourdoughService
import io.ktor.http.*
import io.ktor.server.plugins.ratelimit.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.Route
import kotlin.text.toIntOrNull

fun Route.sourdoughRoutesV1(sourdoughService: SourdoughService) {
    rateLimit(RateLimitName("protected")) {
        /**
         * User creates a new sourdough.
         */
        post<Sourdough> {
            val dto = call.receive<CreateSourdoughDTO>()

            // ToDo: Auth / JWT Implementierung > userID wird dann von dort geholt
            val userId =
                call.request.headers["user-id"]?.toIntOrNull() ?: return@post call.respond(HttpStatusCode.BadRequest)
            val createdSourdough = sourdoughService.createSourdough(dto = dto, userId = userId)
            call.respond(
                createdSourdough,
            )
        }

        /**
         * User retrieves data of all of his sourdoughs.
         */
        get<Sourdough> { sourdough ->
            val sortParam = sourdough.sort
            call.respondText(
                text = "These are all of your sourdoughs -> sorted by: $sortParam", status = HttpStatusCode.OK
            )
        }

        /**
         * User retrieves data of all available sourdough containers.
         * Examples: Glass, Jar,...
         */
        get<Sourdough.Container> { sourdough ->
            val sortParam = sourdough.sort
            call.respondText(
                text = "These are all of our available sourdough containers -> sorted by: $sortParam",
                status = HttpStatusCode.OK
            )
        }

        /**
         * User retrieves data of one specific sourdough.
         */
        get<Sourdough.Id> { sourdough ->
            val sourdoughId = sourdough.id
            call.respondText(
                text = "Here comes the data for your sourdough with id: $sourdoughId", status = HttpStatusCode.OK
            )
        }

        /**
         * User modifies data of one specific sourdough.
         */
        patch<Sourdough.Id> { sourdough ->
            val sourdoughId = sourdough.id
            call.respondText(
                text = "You modified the sourdough with id: $sourdoughId", status = HttpStatusCode.OK
            )

        }

        /**
         * User deletes one specific sourdough.
         */
        delete<Sourdough.Id> { sourdough ->
            val dto = DeleteSourdoughDTO(
                id = sourdough.id
            )
            val deletedSourdough = sourdoughService.deleteSourdough(dto)
            call.respond(deletedSourdough)
        }

        /**
         * User feeds one specific sourdough.
         */
        post<Sourdough.Id.Feed> { sourdough ->
            val sourdoughId = sourdough.parent.id
            call.respondText(
                text = "You just fed your sourdough with id: $sourdoughId", status = HttpStatusCode.OK
            )
        }

        /**
         * User retrieves the current feeding state of one specific sourdough.
         * Examples: JUST_FED, HUNGRY, STARVING,...
         */
        get<Sourdough.Id.FeedState> { sourdough ->
            val sourdoughId = sourdough.parent.id
            call.respondText(
                text = "These are all feeding states for your sourdough with id: $sourdoughId",
                status = HttpStatusCode.OK
            )
        }

        /**
         * User retrieves the feeding log of one specific sourdough.
         */
        get<Sourdough.Id.FeedLog> { sourdough ->
            val sourdoughId = sourdough.parent.id
            val sort = sourdough.parent.parent.sort
            call.respondText(
                text = "You fed your sourdough with id $sourdoughId on these days: Monday, Tuesday, Saturday - LAST YEAR!!11 -> sorting order: $sort",
                status = HttpStatusCode.OK
            )
        }
    }
}