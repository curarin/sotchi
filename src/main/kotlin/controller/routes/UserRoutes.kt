package app.sotchi.controller.routes

import app.sotchi.controller.resources.UserAuth
import app.sotchi.domain.generic.UserRole
import app.sotchi.dto.user.UserCreateDTO
import app.sotchi.dto.user.UserLoginDTO
import app.sotchi.dto.user.UserReadDTO
import app.sotchi.dto.user.UserUpdateDTO
import app.sotchi.service.UserService
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.cachingheaders.*
import io.ktor.server.plugins.ratelimit.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.Route
import io.ktor.server.routing.application
import java.util.*


fun Route.userRoutesV1(userService: UserService) {
    rateLimit(RateLimitName("public")) {
        /**
         * Creates a new account for a new user.
         */
        post<UserAuth.Create> {
            call.caching = CachingOptions(CacheControl.NoStore(visibility = CacheControl.Visibility.Private))
            val user = call.receive<UserCreateDTO>()
            userService.create(user)
            call.respond(HttpStatusCode.Created)
        }
        /**
         * Login for an existing user.
         */
        post<UserAuth.Login> {
            call.caching = CachingOptions(CacheControl.NoStore(visibility = CacheControl.Visibility.Private))
            val user = call.receive<UserLoginDTO>()
            val loggedInUser = userService.login(user)
            val jwtToken = JWT.create().withAudience(environment.config.property("ktor.jwt.audience").getString())
                .withIssuer(environment.config.property("ktor.jwt.issuer").getString())
                .withClaim("userId", loggedInUser.id)
                .withClaim("role", loggedInUser.role.toString())
                .withExpiresAt(Date(System.currentTimeMillis() + 60000))
                .sign(Algorithm.HMAC256(environment.config.property("ktor.jwt.secret").getString()))
            call.respond(HttpStatusCode.OK, hashMapOf("token" to jwtToken))
        }
    }

    rateLimit(RateLimitName("protected")) {
        authenticate("auth-jwt") {
            /**
             * Returns user profile data.
             */
            get<UserAuth.Read> {
                call.caching = CachingOptions(CacheControl.NoStore(visibility = CacheControl.Visibility.Private))
                val user = call.principal<JWTPrincipal>()
                val userId = user!!.payload.getClaim("userId").asInt()
                val userRole = UserRole.valueOf(user.payload.getClaim("role").asString())
                val expiresAt = user.expiresAt?.time?.minus(System.currentTimeMillis())
                application.environment.log.info("JWT token received: userId $userId, userRole $userRole, expires at $expiresAt ms")
                val existingUser = userService.read(UserReadDTO(userId))
                call.respond(HttpStatusCode.OK, existingUser)
            }

            /**
             * Modification for a user profile.
             */
            patch<UserAuth.Update> {
                call.caching = CachingOptions(CacheControl.NoStore(visibility = CacheControl.Visibility.Private))
                val userWithUpdatedData = call.receive<UserUpdateDTO>()
                val user = call.principal<JWTPrincipal>()
                val userId = user!!.payload.getClaim("userId").asInt()
                application.environment.log.info("JWT token received: userId $userId")
                val userIsModified = userService.update(userId = userId, dto = userWithUpdatedData)
                call.respond(HttpStatusCode.OK, userIsModified)
            }

            /**
             * Deletes a users profile.
             */
            delete<UserAuth.Delete> {
                call.caching = CachingOptions(CacheControl.NoStore(visibility = CacheControl.Visibility.Private))
                val user = call.principal<JWTPrincipal>()
                val userId = user!!.payload.getClaim("userId").asInt()
                userService.delete(userId)
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}
