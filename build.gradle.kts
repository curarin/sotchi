plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.plugin.serialization)
}

group = "app.sotchi"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.swagger)
    implementation(libs.ktor.server.routing.openapi)
    implementation(libs.ktor.server.netty)
    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.kotlin.datetime)
    implementation(libs.exposed.jdbc)
    implementation(libs.h2)
    implementation(libs.kotlinx.datetime)
    implementation(libs.ktor.server.config.yaml)
    implementation(libs.ktor.server.openapi)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.resources)
    testImplementation(libs.ktor.server.test.host)
    testImplementation(kotlin("test"))
    implementation(libs.mkammerer.argon2)
    implementation(libs.ktor.server.status.pages)
    implementation(libs.ktor.server.rate.limit)
    implementation(libs.ktor.server.caching.header)
    implementation(libs.ktor.server.default.headers)

}

tasks.test {
    useJUnitPlatform()
}

ktor {
    openApi {
        enabled = true
        codeInferenceEnabled = true
        onlyCommented = false
    }
}