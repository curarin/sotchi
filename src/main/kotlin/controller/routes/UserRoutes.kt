package app.sotchi.controller.routes

import app.sotchi.controller.resources.UserAuth
import app.sotchi.domain.exception.EmailAlreadyInUseException
import app.sotchi.domain.exception.UserNotAuthenticated
import app.sotchi.domain.exception.UserNotFoundException
import app.sotchi.dto.user.UserCreateDTO
import app.sotchi.dto.user.UserLoginDTO
import app.sotchi.dto.user.UserReadDTO
import app.sotchi.dto.user.UserUpdateDTO
import app.sotchi.service.UserService
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.Route
import java.util.Date


fun Route.userRoutesV1(userService: UserService) {
    /**
     * Returns user profile data.
     */
    get<UserAuth.Read> {
        val user = call.receive<UserReadDTO>()
        try {
            val existingUser = userService.read(user)
            call.respond(HttpStatusCode.OK, existingUser)
        } catch (exception: UserNotFoundException) {
            call.respond(HttpStatusCode.NotFound, exception.message ?: "User not found")
        }
    }

    /**
     * Creates a new account for a new user.
     */
    post<UserAuth.Create> {
        val user = call.receive<UserCreateDTO>()
        try {
            userService.create(user)
            call.respond(HttpStatusCode.Created)
        } catch (exception: EmailAlreadyInUseException) {
            call.respond(HttpStatusCode.Conflict, exception.message ?: "Email already in use.")
        }
    }
    /**
     * Login for an existing user.
     */
    post<UserAuth.Login> {
        val user = call.receive<UserLoginDTO>()
        try {
            val loggedInUser = userService.login(user)
            val jwtToken = JWT.create()
                .withAudience(environment.config.property("ktor.jwt.audience").getString())
                .withIssuer(environment.config.property("ktor.jwt.issuer").getString())
                .withClaim("userId", loggedInUser.id)
                .withClaim("role", loggedInUser.role.toString())
                .withExpiresAt(Date(System.currentTimeMillis() + 60000))
                .sign(Algorithm.HMAC256(environment.config.property("ktor.jwt.secret").getString()))
            call.respond(HttpStatusCode.OK, hashMapOf("token" to jwtToken))
        } catch (exception: UserNotFoundException) {
            call.respond(HttpStatusCode.NotFound, exception.message ?: "User not found")
        } catch (exception: UserNotAuthenticated) {
            call.respond(HttpStatusCode.Unauthorized, exception.message ?: "User not authenticated.")
        }
    }

    /**
     * Modification for a user profile.
     */
    patch<UserAuth.Update> {
        val user = call.receive<UserUpdateDTO>()
        val userId = user.id
        try {
            val userIsModified = userService.update(userId = userId, dto = user)
            call.respond(HttpStatusCode.OK, userIsModified)
        } catch (exception: UserNotFoundException) {
            call.respond(HttpStatusCode.NotFound, exception.message ?: "User not found.")
        } catch (exception: EmailAlreadyInUseException) {
            call.respond(HttpStatusCode.Conflict, exception.message ?: "Email already in use.")
        }
    }

    /**
     * Deletes a users profile.
     */
    delete<UserAuth.Delete> {
        val user = call.receive<UserReadDTO>()
        val userId = user.id
        try {
            userService.delete(userId)
            call.respond(HttpStatusCode.OK)
        } catch (exception: UserNotFoundException) {
            call.respond(HttpStatusCode.NotFound, exception.message ?: "User not found.")
        }
    }
}
