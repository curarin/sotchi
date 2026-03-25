package app.sotchi.controller.resources

import io.ktor.resources.*

@Resource("/sourdough")
class Sourdough(val sort: String? = "desc") {
    /**
     * Resource for a specific sourdough by ID.
     */
    @Resource("{id}")
    class Id(val parent: Sourdough = Sourdough(), val id: Int) {
        /**
         * Resource for feeding a specific user's sourdough.
         */
        @Resource("feed")
        class Feed(val parent: Id)

        /**
         * Resource for feeding state of a specific user's sourdough.
         */
        @Resource("feed-state")
        class FeedState(val parent: Id)

        /**
         * Resource for retrieving the feed log of a specific user's sourdough.
         */
        @Resource("feed-log")
        class FeedLog(val parent: Id)
    }

    /**
     * Resource for all available sourdough containers.
     */
    @Resource("container")
    class Container(val parent: Sourdough = Sourdough(), val sort: String? = "asc")
}