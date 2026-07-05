package app.sotchi.controller.resources

import io.ktor.resources.*

@Resource("/sourdough")
class Sourdough() {

    @Resource("create")
    class Create(val parent: Sourdough = Sourdough())

    @Resource("update")
    class Update(val parent: Sourdough = Sourdough())

    @Resource("delete")
    class Delete(val parent: Sourdough = Sourdough())

    @Resource("feed")
    class Feed(val parent: Sourdough = Sourdough())

    @Resource("read")
    class Read(val parent: Sourdough = Sourdough())

    @Resource("readAll")
    class ReadAll(val parent: Sourdough = Sourdough(), val sort: String? = "desc")
}