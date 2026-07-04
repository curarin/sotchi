package app.sotchi.service

import app.sotchi.domain.exception.SourdoughNameInvalid
import app.sotchi.domain.exception.UserEmailInvalid
import app.sotchi.domain.exception.UserNameInvalid
import app.sotchi.domain.exception.UserPasswordInvalid

/**
 * Generic class / object which validates all possible inputs by the app consumer.
 */
object InputValidator {


    /**
     * Validates that the user tried to set a valid / invalid password
     * @param password: User defined password to be validated
     * @return Password is valid (true) or invalid (false)
     */
    fun validatePassword(password: String) {
        val passwordLength = password.length
        if (passwordLength !in 8..64) {
            throw UserPasswordInvalid("Passwords must be between 8 and 64 characters")
        }
    }

    /**
     * Validates that the user tried to set a valid / invalid email
     * @param email: User defined email to be validated
     * @return Email address is valid (true) or invalid (false)
     */
    fun validateEmail(email: String) {
        val emailLength = email.length
        val countAtSigns = email.count { char -> char == '@' }
        val countDotChars = email.count { char -> char == '.' }

        if (countAtSigns > 1 || countAtSigns == 0) {
            throw UserEmailInvalid("Email must contain at maximum one '@' character")
        } else if (countDotChars > 2) {
            throw UserEmailInvalid("Email must contain at maximum three '.' characters")
        } else if (emailLength !in 1..64) {
            throw UserEmailInvalid("Email must be between 1 and 64 characters")
        }
    }

    /**
     * Validates that the user tried to set a valid / invalid username
     * @param username: User defined name to be validated
     * @return Username is valid (true) or invalid (false)
     */
    fun validateUsername(username: String) {
        val nameLength = username.length
        val hasOnlyAsciiChars = Regex("^[a-zA-Z0-9]*$").containsMatchIn(username)

        if (nameLength !in 3..30) {
            throw UserNameInvalid("Username must contain at least 3 and at most 30 characters")
        } else if (username.startsWith(" ") || username.endsWith(" ")) {
            throw UserNameInvalid("Username must not start or end with empty spaces")
        } else if (!hasOnlyAsciiChars) {
            throw UserNameInvalid("Username must only contain ASCII characters")
        }
    }

    /**
     * Validates that the user tries to set a valid / invalid sourdough name
     * @param sourdoughName: User defined sourdough name
     * @return Sourdough name is valid (true) or invalid (false)
     */
    fun validateSourdoughName(sourdoughName: String) {
        val sourdoughLength = sourdoughName.length
        val hasOnlyAsciiChars = Regex("^[a-zA-Z0-9]*$").containsMatchIn(sourdoughName)

        if (sourdoughLength !in 3..30) {
            throw SourdoughNameInvalid("Sourdough name must contain at least 3 and at most 30 characters")
        } else if (sourdoughName.startsWith(" ") || sourdoughName.endsWith(" ")) {
            throw SourdoughNameInvalid("Sourdough name must not start or end with empty spaces")
        } else if (!hasOnlyAsciiChars) {
            throw SourdoughNameInvalid("Sourdough name must only contain ASCII characters")
        }
    }
}