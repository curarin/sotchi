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
    implementation(libs.logback.classic)
    implementation(libs.exposed.core)
    implementation("org.jetbrains.exposed:exposed-dao:1.1.1")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:1.1.1")
    implementation(libs.exposed.jdbc)
    implementation(libs.h2)
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")
    implementation(libs.ktor.server.config.yaml)
    implementation("io.ktor:ktor-server-openapi")
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.resources)
    implementation("io.ktor:ktor-client-content-negotiation:3.4.1")
    testImplementation(libs.ktor.server.test.host)
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("de.mkammerer:argon2-jvm:2.12")

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