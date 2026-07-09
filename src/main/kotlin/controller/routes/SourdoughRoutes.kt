package app.sotchi.controller.routes

import app.sotchi.controller.resources.Sourdough
import app.sotchi.dto.sourdough.SourdoughCreateDTO
import app.sotchi.dto.sourdough.SourdoughDeleteDTO
import app.sotchi.dto.sourdough.SourdoughFeedDTO
import app.sotchi.dto.sourdough.SourdoughReadDTO
import app.sotchi.dto.sourdough.SourdoughUpdateDTO
import app.sotchi.service.SourdoughService
import io.ktor.http.*
import io.ktor.http.content.CachingOptions
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.cachingheaders.caching
import io.ktor.server.plugins.ratelimit.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.Route
import io.ktor.server.routing.application

fun Route.sourdoughRoutesV1(sourdoughService: SourdoughService) {
    rateLimit(RateLimitName("protected")) {
        authenticate("auth-jwt") {
            /**
             * User creates a new sourdough
             */
            post<Sourdough.Create> {
                call.caching = CachingOptions(CacheControl.NoStore(visibility = CacheControl.Visibility.Private))
                val dto = call.receive<SourdoughCreateDTO>()
                val user = call.principal<JWTPrincipal>()
                val userId = user!!.payload.getClaim("userId").asInt()
                application.environment.log.info("UserId $userId creates sourdough '${dto.name}'")
                sourdoughService.create(dto, userId)
                call.respond(HttpStatusCode.Created)
            }

            /**
             * User deletes their existing sourdough
             */
            delete<Sourdough.Delete> {
                call.caching = CachingOptions(CacheControl.NoStore(visibility = CacheControl.Visibility.Private))
                val dto = call.receive<SourdoughDeleteDTO>()
                val user = call.principal<JWTPrincipal>()
                val userId = user!!.payload.getClaim("userId").asInt()
                application.environment.log.info("UserId $userId deletes sourdough '${dto.id}'")
                sourdoughService.delete(dto, userId)
                call.respond(HttpStatusCode.OK)
            }

            /**
             * User updates their existing sourdough
             */
            patch<Sourdough.Update> {
                call.caching = CachingOptions(CacheControl.NoStore(visibility = CacheControl.Visibility.Private))
                val dto = call.receive<SourdoughUpdateDTO>()
                val user = call.principal<JWTPrincipal>()
                val userId = user!!.payload.getClaim("userId").asInt()
                application.environment.log.info("UserId $userId updates sourdough '${dto.id}'")
                val sourdoughIsModified = sourdoughService.update(dto, userId)
                call.respond(HttpStatusCode.OK, sourdoughIsModified)
            }

            /**
             * User reads one specific existing sourdough
             */
            get<Sourdough.Read> {
                call.caching = CachingOptions(CacheControl.NoStore(visibility = CacheControl.Visibility.Private))
                val dto = call.receive<SourdoughReadDTO>()
                application.environment.log.info("Sourdough '${dto.id}' requested")
                val foundSourdough = sourdoughService.read(dto)
                call.respond(HttpStatusCode.OK, foundSourdough)
            }

            /**
             * User reads all of their existing sourdoughs
             */
            get<Sourdough.ReadAll> {
                val user = call.principal<JWTPrincipal>()
                val userId = user!!.payload.getClaim("userId").asInt()
                application.environment.log.info("UserId $userId requests all their sourdoughs")
                val foundSourdoughs = sourdoughService.readAll(userId)
                call.respond(HttpStatusCode.OK, foundSourdoughs)
            }

            /**
             * User feeds one of their existing sourdoughs
             */
            post<Sourdough.Feed> {
                val user = call.principal<JWTPrincipal>()
                val dto = call.receive<SourdoughFeedDTO>()
                val userId = user!!.payload.getClaim("userId").asInt()
                application.environment.log.info("UserId $userId feeds sourdough '${dto.id}'")
                sourdoughService.feed(dto, userId)
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}