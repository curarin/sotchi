package app.sotchi.domain.exception

open class UserInputValidationException(message: String) : RuntimeException(message)

class UserPasswordInvalid(message: String) : UserInputValidationException(message)
class UserEmailInvalid(message: String) : UserInputValidationException(message)
class UserNameInvalid(message: String) : UserInputValidationException(message)