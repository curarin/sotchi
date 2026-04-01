package security

import app.sotchi.security.Encryption
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertTrue

class AuthenticationTest {
    private lateinit var authenticator: Encryption
    private lateinit var password: CharArray

   @BeforeEach
   fun setUp() {
       authenticator = Encryption()
       password = charArrayOf('a', 'b', 'c')
   }

   @Test
   fun `hashed password is validated by Argon2`() {
       val hashedPassword = authenticator.hashPassword(password)
       assertTrue(authenticator.validate(hashedPassword, password))
   }

}