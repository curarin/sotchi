package app.sotchi.domain.exception

open class SourdoughException(message: String) : RuntimeException(message)

class SourdoughNotFoundException() : SourdoughException("Sourdough not found")