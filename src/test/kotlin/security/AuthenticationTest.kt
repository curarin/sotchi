package security

import app.sotchi.security.Authentication
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertTrue

class AuthenticationTest {
    private lateinit var authenticator: Authentication
    private lateinit var password: CharArray

   @BeforeEach
   fun setUp() {
       authenticator = Authentication()
       password = charArrayOf('a', 'b', 'c')
   }

   @Test
   fun `hashed password is validated by Argon2`() {
       val hashedPassword = authenticator.hashPassword(password)
       assertTrue(authenticator.validate(hashedPassword, password))
   }

}