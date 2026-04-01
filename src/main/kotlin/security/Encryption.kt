package app.sotchi.security
import de.mkammerer.argon2.Argon2
import de.mkammerer.argon2.Argon2Factory

class Encryption() {
    val argon2: Argon2 = Argon2Factory.create()

    fun validate(storedHash: String, password: CharArray): Boolean {
        return argon2.verify(storedHash, password)
    }

    fun hashPassword(password: CharArray): String {
        // fun hash(iterations: Int, memory: Int, parallelism: Int, password: String!): String!
        return argon2.hash(2, 65536, 1, password)
    }
}