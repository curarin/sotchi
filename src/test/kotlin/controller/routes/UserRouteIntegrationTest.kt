package controller.routes

import app.sotchi.dto.user.UserCreateDTO
import app.sotchi.dto.user.UserLoginDTO
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.jupiter.api.Test
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UserRouteIntegrationTest {
    private lateinit var name: String
    private lateinit var email: String
    private lateinit var password: String
    private fun randomEmail() = "test-${System.currentTimeMillis()}@example.com"


    @BeforeTest
    fun setup() {
        name = "testUser"
        email = "test-${System.currentTimeMillis()}@example.com"
        password = "test123456"

    }

    /**
     * Integration Test
     * Validates the following user journey:
     *  - User account can get created
     *  - User is able to log in
     *  - User data is fetched for profile page or smth
     *  - User profile is deleted
     */
    @Test
    fun `user journey validation - creation, login, fetch, delete`() = withTestApplication { client ->
        val responseUserCreated = client.post("/api/v1/user/auth/create") {
            contentType(ContentType.Application.Json)
            setBody(
                UserCreateDTO(name, email, password)
            )
        }
        assertEquals(HttpStatusCode.Created, responseUserCreated.status)

        val responseUserLoggedIn = client.post("/api/v1/user/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(
                UserLoginDTO(email, password)
            )
        }
        val json = Json.parseToJsonElement(responseUserLoggedIn.bodyAsText()).jsonObject
        val token = json["token"]?.jsonPrimitive?.content
        println(token)

        assertTrue(token?.startsWith("ey") ?: false)
        assertEquals(HttpStatusCode.OK, responseUserLoggedIn.status)

        val responseUserProfile = client.get("/api/v1/user/auth/read") {
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Authorization, "Bearer $token")
        }
        val userProfileJson = Json.parseToJsonElement(responseUserProfile.bodyAsText()).jsonObject
        assertEquals(name, userProfileJson["name"]?.jsonPrimitive?.content ?: "")
        assertEquals(email, userProfileJson["email"]?.jsonPrimitive?.content ?: "")
        assertFalse(userProfileJson["isActivated"]?.jsonPrimitive?.content?.toBoolean() ?: false)

        val responseUserDeletion = client.delete("/api/v1/user/auth/delete") {
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Authorization, "Bearer $token")
        }
        assertEquals(HttpStatusCode.OK, responseUserDeletion.status)

        val responseUserLoginAfterDeletion = client.get("/api/v1/user/auth/read") {
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Authorization, "Bearer $token")
        }
        assertEquals(HttpStatusCode.Unauthorized, responseUserLoginAfterDeletion.status)
    }

    /**
     * Integration Test
     *
     * Validates that authentication fails when an incorrect password is provided.
     *
     * Ensures that:
     * - A valid user exists
     * - Login with wrong password returns HTTP 401 Unauthorized
     */
    @Test
    fun `login with invalid password returns 401`() = withTestApplication { client ->
        val email = randomEmail()

        client.post("/api/v1/user/auth/create") {
            contentType(ContentType.Application.Json)
            setBody(UserCreateDTO(name, email, password))
        }

        val response = client.post("/api/v1/user/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(UserLoginDTO(email, "wrongPassword"))
        }

        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    /**
     * Integration Test
     *
     * Validates that login fails when the email is not registered.
     *
     * Ensures that:
     * - Login attempt with unknown email returns HTTP 401 Unauthorized
     */
    @Test
    fun `login with unknown email returns 401`() = withTestApplication { client ->
        val response = client.post("/api/v1/user/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(UserLoginDTO(randomEmail(), password))
        }

        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    /**
     * Integration Test
     *
     * Validates that protected endpoints require authentication.
     *
     * Ensures that:
     * - Accessing profile without Authorization header returns HTTP 401 Unauthorized
     */
    @Test
    fun `read profile without token returns 401`() = withTestApplication { client ->
        val response = client.get("/api/v1/user/auth/read")

        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    /**
     * Integration Test
     *
     * Validates that invalid tokens are rejected by protected endpoints.
     *
     * Ensures that:
     * - A malformed or fake JWT token returns HTTP 401 Unauthorized
     */
    @Test
    fun `read profile with invalid token returns 401`() = withTestApplication { client ->
        val response = client.get("/api/v1/user/auth/read") {
            header(HttpHeaders.Authorization, "Bearer invalid.token.here")
        }

        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    /**
     * Integration Test
     *
     * Validates that duplicate user registration is not allowed.
     *
     * Ensures that:
     * - Creating a user with an already used email returns HTTP 400 Bad Request
     *   (or 409 Conflict depending on implementation)
     */
    @Test
    fun `create user with duplicate email returns 400`() = withTestApplication { client ->
        val email = randomEmail()

        val first = client.post("/api/v1/user/auth/create") {
            contentType(ContentType.Application.Json)
            setBody(UserCreateDTO(name, email, password))
        }
        assertEquals(HttpStatusCode.Created, first.status)

        val second = client.post("/api/v1/user/auth/create") {
            contentType(ContentType.Application.Json)
            setBody(UserCreateDTO(name, email, password))
        }

        assertEquals(HttpStatusCode.BadRequest, second.status)
    }

    /**
     * Integration Test
     *
     * Validates input validation for email format.
     *
     * Ensures that:
     * - Creating a user with an invalid email returns HTTP 400 Bad Request
     */
    @Test
    fun `create user with invalid email returns 400`() = withTestApplication { client ->
        val response = client.post("/api/v1/user/auth/create") {
            contentType(ContentType.Application.Json)
            setBody(UserCreateDTO(name, "invalid-email", password))
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    /**
     * Integration Test
     *
     * Validates password strength requirements.
     *
     * Ensures that:
     * - Creating a user with a weak password returns HTTP 400 Bad Request
     */
    @Test
    fun `create user with weak password returns 400`() = withTestApplication { client ->
        val response = client.post("/api/v1/user/auth/create") {
            contentType(ContentType.Application.Json)
            setBody(UserCreateDTO(name, randomEmail(), "123"))
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }
}