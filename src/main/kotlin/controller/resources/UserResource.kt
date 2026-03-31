package app.sotchi.controller.resources

import io.ktor.resources.Resource

// We want type safe routing, so we do https://ktor.io/docs/server-resources.html
@Resource("/user/auth")
class UserAuth() {
    @Resource("read")
    class Read(val parent: UserAuth = UserAuth())

    @Resource("create")
    class Create(val parent: UserAuth = UserAuth())

    @Resource("login")
    class Login(val parent: UserAuth = UserAuth())

    @Resource("update")
    class Update(val parent: UserAuth = UserAuth())

    @Resource("delete")
    class Delete(val parent: UserAuth = UserAuth())
}