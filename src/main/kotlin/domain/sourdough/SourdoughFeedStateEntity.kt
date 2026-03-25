package app.sotchi.domain.sourdough

import kotlinx.serialization.Serializable

@Serializable
enum class SourdoughFeedStateEntity {
    HUNGRY,
    STARVING,
    FED
}