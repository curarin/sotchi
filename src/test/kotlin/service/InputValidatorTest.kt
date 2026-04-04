package service

import app.sotchi.domain.exception.UserEmailInvalid
import app.sotchi.domain.exception.UserNameInvalid
import app.sotchi.domain.exception.UserPasswordInvalid
import app.sotchi.service.InputValidator
import kotlin.test.Test
import kotlin.test.assertFailsWith

/**
 * Tests the input validator for all sorts of input cases
 */
class InputValidatorTest {

    @Test
    fun `password too short`() {
        assertFailsWith<UserPasswordInvalid> {
            val password = "a".repeat(7)
            InputValidator.validatePassword(password)
        }
    }

    @Test
    fun `password too long`() {
        assertFailsWith<UserPasswordInvalid> {
            val password = "a".repeat(65)
            InputValidator.validatePassword(password)
        }
    }

    @Test
    fun `email is too long`() {
        assertFailsWith<UserEmailInvalid> {
            val emailOutOufBounds = "a".repeat(256)
            InputValidator.validateEmail(emailOutOufBounds)
        }
    }

    @Test
    fun `email has more than 1 @ sign`() {
        val invalidEmailList = listOf(
            "test@@example.com",
            "test@examp@le.com",
            "test@@@@exampl@@@e.com"
        )
        for (email in invalidEmailList) {
            assertFailsWith<UserEmailInvalid> {
                InputValidator.validateEmail(email)
            }
        }
    }

    @Test
    fun `email has no @ sign`() {
        assertFailsWith<UserEmailInvalid> {
            InputValidator.validateEmail("testexample.com")
        }
    }

    @Test
    fun `email has incorrect tld`() {
        val invalidEmailList = listOf(
            "test@example",
            "test@example.",
            "test@example.."
        )
        for (email in invalidEmailList) {
            assertFailsWith<UserEmailInvalid> {
                InputValidator.validateEmail(email)
            }
        }
    }

    @Test
    fun `name is too short`() {
        assertFailsWith<UserNameInvalid> {
            InputValidator.validateUsername("a".repeat(2))
        }

    }

    @Test
    fun `name is too long`() {
        assertFailsWith<UserNameInvalid> {
            InputValidator.validateUsername("a".repeat(31))
        }
    }

    @Test
    fun `name has unallowed signs`() {
        val invalidUsernames = listOf(
            "über",
            "Œgart",
            "Pablo-123",
            "max mustermann",
            "john@doe",
            "name!",
            "name#",
            "name/"
        )

        invalidUsernames.forEach { username ->
            assertFailsWith<UserNameInvalid> {
                InputValidator.validateUsername(username)
            }
        }
    }

    @Test
    fun `name has unicode characters`() {
        val invalidUsernames = listOf(
            "🔥🔥🔥",                    // emojis only
            "   ",                      // whitespace
            "\u0000",                   // null char
            "test\u0007",               // bell control char
            "name\u200Bhidden",         // zero-width space
            "‮evil",                    // RTL override char
            "👨‍👩‍👧‍👦",                       // complex emoji (family)
            "a\u0301",                  // combining character (á composed)
            "\uD83D",                   // broken surrogate
            "name\nnewline"             // newline injection
        )
        for (username in invalidUsernames) {
            assertFailsWith<UserNameInvalid> {
                InputValidator.validateUsername(username)
            }
        }
    }

    @Test
    fun `name starts with space`() {
        assertFailsWith<UserNameInvalid> {
            InputValidator.validateUsername(" abc")
        }
    }

    @Test
    fun `name ends with space`() {
        assertFailsWith<UserNameInvalid> {
            InputValidator.validateUsername("abc ")
        }
    }

    @Test
    fun `name has multiple consecutive special chars`() {
        val unallowedUserNames = listOf(
            "abcd//",
            "abc()defg",
            "abc$$",
            "abc%%%%",
            "abcd'''",
            "abcde=====",
            "ab=cd==fg",
            "A!!bcd",
            "abcd??????",
            "a...b....c...."
        )

        for (username in unallowedUserNames) {
            assertFailsWith<UserNameInvalid> {
                InputValidator.validateUsername(username)
            }
        }
    }
}