package app.sotchi.domain.exception

open class UserException(message: String) : RuntimeException(message)

class UserNotFoundException : UserException("User not found")
class UserNotAuthenticated : UserException("User not authenticated")
class EmailAlreadyInUseException : UserException("Email already in use")