package controller.routes

import app.sotchi.domain.generic.FlourTypeEntity
import app.sotchi.domain.generic.LiquidTypeEntity
import app.sotchi.domain.generic.SourdoughHealthState
import app.sotchi.dto.sourdough.*
import app.sotchi.dto.user.UserCreateDTO
import app.sotchi.dto.user.UserLoginDTO
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class SourdoughRouteIntegrationTest {

    @Test
    fun `sourdough gets created`() = withTestApplication { client ->
        val email = "test-${System.currentTimeMillis()}@example.com"
        val password = "test123456"
        val userName = "testUser"

        client.post("/api/v1/user/auth/create") {
            contentType(ContentType.Application.Json)
            setBody(
                UserCreateDTO(userName, email, password)
            )
        }

        val responseUserLoggedIn = client.post("/api/v1/user/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(
                UserLoginDTO(email, password)
            )
        }
        val json = Json.parseToJsonElement(responseUserLoggedIn.bodyAsText()).jsonObject
        val token = json["token"]?.jsonPrimitive?.content!!

        val response = client.post("/api/v1/sourdough/create") {
            contentType(ContentType.Application.Json)
            setBody(
                SourdoughCreateDTO(
                    name = "SauerHelge",
                    flourType = FlourTypeEntity.WHITE_WHOLE_WHEAT,
                    liquidType = LiquidTypeEntity.WATER,
                    healthState = SourdoughHealthState.JUST_FED
                )
            )
            header(HttpHeaders.Authorization, "Bearer $token")
        }
        assertEquals(HttpStatusCode.Created, response.status)

    }

    @Test
    fun `sourdough gets updated`() = withTestApplication { client ->
        val email = "test-${System.currentTimeMillis()}@example.com"
        val password = "test123456"
        val userName = "testUser"

        client.post("/api/v1/user/auth/create") {
            contentType(ContentType.Application.Json)
            setBody(
                UserCreateDTO(userName, email, password)
            )
        }

        val responseUserLoggedIn = client.post("/api/v1/user/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(
                UserLoginDTO(email, password)
            )
        }
        val json = Json.parseToJsonElement(responseUserLoggedIn.bodyAsText()).jsonObject
        val token = json["token"]?.jsonPrimitive?.content!!

        val response = client.post("/api/v1/sourdough/create") {
            contentType(ContentType.Application.Json)
            setBody(
                SourdoughCreateDTO(
                    name = "SauerHelge",
                    flourType = FlourTypeEntity.WHITE_WHOLE_WHEAT,
                    liquidType = LiquidTypeEntity.WATER,
                    healthState = SourdoughHealthState.JUST_FED
                )
            )
            header(HttpHeaders.Authorization, "Bearer $token")
        }
        assertEquals(HttpStatusCode.Created, response.status)

        val updateResponse = client.patch("/api/v1/sourdough/update") {
            contentType(ContentType.Application.Json)
            setBody(
                SourdoughUpdateDTO(
                    1, name = "SauerHelgeDerZweite"
                )
            )
            header(HttpHeaders.Authorization, "Bearer $token")
        }
        assertEquals(HttpStatusCode.OK, updateResponse.status)
        assertTrue(updateResponse.body())

        val allSourdoughsAfterUpdate: List<SourdoughProfileDTO> = client.get("/api/v1/sourdough/readAll") {
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Authorization, "Bearer $token")
        }.body()
        assertEquals(1, allSourdoughsAfterUpdate.size)
        assertEquals("SauerHelgeDerZweite", allSourdoughsAfterUpdate.first().sourdoughName)
    }

    @Test
    fun `sourdough gets deleted`() = withTestApplication { client ->
        val email = "test-${System.currentTimeMillis()}@example.com"
        val password = "test123456"
        val userName = "testUser"

        client.post("/api/v1/user/auth/create") {
            contentType(ContentType.Application.Json)
            setBody(
                UserCreateDTO(userName, email, password)
            )
        }

        val responseUserLoggedIn = client.post("/api/v1/user/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(
                UserLoginDTO(email, password)
            )
        }
        val json = Json.parseToJsonElement(responseUserLoggedIn.bodyAsText()).jsonObject
        val token = json["token"]?.jsonPrimitive?.content!!

        // Gets created
        val response = client.post("/api/v1/sourdough/create") {
            contentType(ContentType.Application.Json)
            setBody(
                SourdoughCreateDTO(
                    name = "SauerHelge",
                    flourType = FlourTypeEntity.WHITE_WHOLE_WHEAT,
                    liquidType = LiquidTypeEntity.WATER,
                    healthState = SourdoughHealthState.JUST_FED
                )
            )
            header(HttpHeaders.Authorization, "Bearer $token")
        }
        assertEquals(HttpStatusCode.Created, response.status)

        // We delete one specifically
        val deletionResponse = client.delete("/api/v1/sourdough/delete") {
            contentType(ContentType.Application.Json)
            setBody(
                SourdoughDeleteDTO(
                    1 // Hardcoded weils im Test Context isoliert ist und daher immer id 1
                )
            )
            header(HttpHeaders.Authorization, "Bearer $token")
        }
        assertEquals(HttpStatusCode.OK, deletionResponse.status)

        // We re-read - and its gone
        // We read all sourdoughs
        val allSourdoughsAfterDeletion: List<SourdoughProfileDTO> = client.get("/api/v1/sourdough/readAll") {
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Authorization, "Bearer $token")
        }.body()
        assertEquals(0, allSourdoughsAfterDeletion.size)
    }

    @Test
    fun `sourdough journey validation - creation, update, fetch, deletion`() = withTestApplication { client ->
        val email = "test-${System.currentTimeMillis()}@example.com"
        val password = "test123456"
        val userName = "testUser"
        val sourdoughId = 1 // Hardcoded weils im Test Erwartbar ist

        client.post("/api/v1/user/auth/create") {
            contentType(ContentType.Application.Json)
            setBody(
                UserCreateDTO(userName, email, password)
            )
        }

        val responseUserLoggedIn = client.post("/api/v1/user/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(
                UserLoginDTO(email, password)
            )
        }
        val json = Json.parseToJsonElement(responseUserLoggedIn.bodyAsText()).jsonObject
        val token = json["token"]?.jsonPrimitive?.content!!

        // Gets created
        val response = client.post("/api/v1/sourdough/create") {
            contentType(ContentType.Application.Json)
            setBody(
                SourdoughCreateDTO(
                    name = "SauerHelge",
                    flourType = FlourTypeEntity.WHITE_WHOLE_WHEAT,
                    liquidType = LiquidTypeEntity.WATER,
                    healthState = SourdoughHealthState.JUST_FED
                )
            )
            header(HttpHeaders.Authorization, "Bearer $token")
        }
        assertEquals(HttpStatusCode.Created, response.status)


        // We update one specifically
        val updateResponse = client.patch("/api/v1/sourdough/update") {
            contentType(ContentType.Application.Json)
            setBody(
                SourdoughUpdateDTO(
                    sourdoughId, name = "SauerHelgeDerZweite"
                )
            )
            header(HttpHeaders.Authorization, "Bearer $token")
        }
        assertEquals(HttpStatusCode.OK, updateResponse.status)
        assertTrue(updateResponse.body())


        // We feed one sourdough
        val feedResponse = client.post("/api/v1/sourdough/feed") {
            contentType(ContentType.Application.Json)
            setBody(
                SourdoughFeedDTO(
                    sourdoughId,
                )
            )
            header(HttpHeaders.Authorization, "Bearer $token")
        }
        assertEquals(HttpStatusCode.OK, feedResponse.status)

        // We read all sourdoughs after the feeding
        val allSourdoughsAfterFeeding: List<SourdoughProfileDTO> = client.get("/api/v1/sourdough/readAll") {
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Authorization, "Bearer $token")
        }.body()
        assertEquals(1, allSourdoughsAfterFeeding.size)
        assertEquals("SauerHelgeDerZweite", allSourdoughsAfterFeeding.first().sourdoughName)
        assertNotNull(allSourdoughsAfterFeeding.first().lastFedAtDt)
    }
}