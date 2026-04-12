package app.sotchi.plugins

import app.sotchi.domain.exception.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.MissingFieldException
import kotlinx.serialization.SerializationException

@OptIn(ExperimentalSerializationApi::class)
fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<BadRequestException> { call, cause ->
            call.application.log.warn("Bad request: ${cause.message}", cause)

            val root = cause.cause?.cause ?: cause.cause

            val message = when (root) {
                is MissingFieldException -> {
                    "Missing field: ${root.missingFields}"
                }

                is SerializationException -> {
                    "Invalid field type or format"
                }

                else -> {
                    root?.message ?: "Invalid request body"
                }
            }

            call.respond(
                HttpStatusCode.BadRequest,
                mapOf(
                    "error" to "INVALID_REQUEST",
                    "message" to message
                )
            )
        }

        exception<Throwable> { call, cause ->
            when (cause) {
                is EmailAlreadyInUseException -> {
                    call.respondText(text = "400: $cause", status = HttpStatusCode.BadRequest)
                }

                is UserNotAuthenticated -> {
                    call.respondText(text = "401: User not authorized", status = HttpStatusCode.Unauthorized)
                }

                is UserNotFoundException -> {
                    call.respondText(text = "401: User not authorized", status = HttpStatusCode.Unauthorized)
                }

                is UserNameInvalid -> {
                    call.respondText(text = "400: $cause", status = HttpStatusCode.BadRequest)
                }

                is UserEmailInvalid -> {
                    call.respondText(text = "400: $cause", status = HttpStatusCode.BadRequest)
                }

                is UserPasswordInvalid -> {
                    call.respondText(text = "400: $cause", status = HttpStatusCode.BadRequest)
                }

                else -> {
                    call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
                }
            }
        }
    }

}