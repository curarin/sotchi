package app.sotchi.domain.exception

open class SourdoughInputValidationException(message: String) : RuntimeException(message)

class SourdoughNameInvalid(message: String) : SourdoughInputValidationException(message)