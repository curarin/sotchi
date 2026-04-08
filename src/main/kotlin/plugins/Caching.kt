package app.sotchi.plugins

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cachingheaders.*
import io.ktor.server.sessions.Cache

/**
 * As mentioned in this article -> https://www.debugbear.com/docs/http-cache-control-header
 * We implemented it to be NoCache per default
 * And for user-sensitive data or user-specific data we restrict it further by going NoStore on route scope
 */
fun Application.configureCaching() {
    install(CachingHeaders) {
        options { call, outgoingContent ->
            CachingOptions(CacheControl.NoCache(visibility = CacheControl.Visibility.Private))
        }
    }
}