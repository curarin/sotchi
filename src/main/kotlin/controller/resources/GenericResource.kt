package app.sotchi.controller.resources

import io.ktor.resources.Resource

/**
 * Resource for generic data stuff which is reused across the data model - e.g. Flour Type.
 */
@Resource("generics")
class Generic() {
    @Resource("flour-type")
    class FlourType(val parent: Generic = Generic())
}