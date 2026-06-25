package app.sotchi.service

import app.sotchi.domain.generic.SourdoughHealthState
import app.sotchi.domain.sourdough.SourdoughEntity
import kotlin.time.Clock

/**
 * State Machine which calculates the health state of our sourdough
 */
class SourdoughHealthStateMachine(val sourdough: SourdoughEntity) {

    /**
     * Traverses through the state machine and calculates the current state
     * based on the days since the last time it was fed
     */
    fun traverse(): SourdoughHealthState {
        val currentTime = Clock.System.now()

        if (sourdough.lastFedAtDt == null) {
            return SourdoughHealthState.UNDEFINED
        }

        val daysSinceLastFed = (currentTime - sourdough.lastFedAtDt).inWholeDays

        if (daysSinceLastFed in 0..3) {
            return SourdoughHealthState.JUST_FED
        } else if (daysSinceLastFed in 4..7) {
            return SourdoughHealthState.HUNGRY
        } else if (daysSinceLastFed in 8..9) {
            return SourdoughHealthState.WEAK
        } else if (daysSinceLastFed in 10..14) {
            return SourdoughHealthState.TOXIC
        } else {
            return SourdoughHealthState.DEAD
        }
    }
}