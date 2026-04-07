package app.sotchi.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.ratelimit.*
import kotlin.time.Duration.Companion.seconds

fun Application.configureRatelimit() {
    install(RateLimit) {

        /**
         * Rate limits used for protected api points, which require authentication to use.
         */
        register(RateLimitName("protected")) {
            rateLimiter(limit = 5, refillPeriod = 60.seconds)
        }

        /**
         * Rate Limits used for public api endpoints, which require no prior authentication to use.
         * This is more restricted on purpose.
         */
        register(RateLimitName("public")) {
            rateLimiter(limit = 3, refillPeriod = 120.seconds)
        }
    }

}