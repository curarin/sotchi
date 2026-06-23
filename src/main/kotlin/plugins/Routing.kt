package app.sotchi.plugins

import app.sotchi.controller.routes.genericRoutesV1
import app.sotchi.controller.routes.healthRouteV1
import app.sotchi.controller.routes.sourdoughRoutesV1
import app.sotchi.controller.routes.userRoutesV1
import app.sotchi.messaging.DefaultEventPublisher
import app.sotchi.messaging.MessagQueueSubscriber
import app.sotchi.repository.SourdoughRepositoryImpl
import app.sotchi.repository.UserRepositoryDbImpl
import app.sotchi.service.SourdoughService
import app.sotchi.service.UserService
import io.ktor.http.*
import io.ktor.openapi.*
import io.ktor.server.application.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.routing.*
import io.ktor.server.routing.openapi.*

fun Application.configureRouting() {
    routing {
        val eventPublisher = DefaultEventPublisher()
        eventPublisher.subscribe(MessagQueueSubscriber())

        val sourdoughRepository = SourdoughRepositoryImpl()
        val userRepository = UserRepositoryDbImpl()
        val sourdoughService = SourdoughService(
            sourdoughRepository = sourdoughRepository,
            userRepository = userRepository,
        )

        val userService = UserService(
            userRepository = userRepository,
            eventPublisher = eventPublisher
        )

        route("/api/v1") {
            healthRouteV1()
            userRoutesV1(userService)
            genericRoutesV1()
            sourdoughRoutesV1(sourdoughService)
        }
        swaggerUI("/swagger") {
            info = OpenApiInfo("SOTCHI - Sourdough API", "0.1")
            source = OpenApiDocSource.Routing(ContentType.Application.Json) {
                routingRoot.descendants()
            }
        }
    }
}

