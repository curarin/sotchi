package app.sotchi.service

import app.sotchi.domain.sourdough.SourdoughEntity
import app.sotchi.domain.sourdough.SourdoughFeedStateEntity
import app.sotchi.domain.user.UserEntity
import app.sotchi.dto.sourdough.CreateSourdoughDTO
import app.sotchi.dto.sourdough.DeleteSourdoughDTO
import app.sotchi.repository.SourdoughRepository
import app.sotchi.repository.UserRepository
import kotlinx.datetime.*
import kotlin.jvm.Throws

class SourdoughService(
    private val sourdoughRepository: SourdoughRepository,
    private val userRepository: UserRepository,
) {
    /**
     * Business logic for: User creates a new sourdough.
     */
    fun createSourdough(dto: CreateSourdoughDTO, userId: Int): SourdoughEntity {
        val newSourdoughFeedState = SourdoughFeedStateEntity.FED
        val currentDateTime = Clock.System.now()
        val newSourdoughEntity = SourdoughEntity(
            user = userRepository.findById(userId),
            flourType = dto.flourType,
            feedState = newSourdoughFeedState,
            sourdoughName = dto.name,
            createdAtDt = currentDateTime,
            lastTimeFedDt = dto.lastTimeFedDt,
            nextStateChangeAtDt = calculateNextStateDt(
                feedState = newSourdoughFeedState,
                lastTimeFedDt = dto.lastTimeFedDt,
            ),
            lastModifiedAtDt = currentDateTime,
            deletedAtDt = null
        )
        return sourdoughRepository.save(newSourdoughEntity)
    }

    /**
     * Business logic for: User deletes an existing sourdough.
     */
    fun deleteSourdough(dto: DeleteSourdoughDTO): SourdoughEntity {
        val currentDateTime = Clock.System.now()
        val currentSourdough = sourdoughRepository.findById(dto.id)
        currentSourdough.deletedAtDt = currentDateTime
        return sourdoughRepository.delete(currentSourdough)
    }

    /**
     * Business Logic which calculates the Datetime, when the next state change is about to happen.
     */
    fun calculateNextStateDt(feedState: SourdoughFeedStateEntity, lastTimeFedDt: Instant): Instant {
        return when (feedState) {
            SourdoughFeedStateEntity.FED -> lastTimeFedDt.plus(7, DateTimeUnit.DAY, TimeZone.UTC)
            SourdoughFeedStateEntity.HUNGRY -> lastTimeFedDt.plus(3, DateTimeUnit.DAY, TimeZone.UTC)
            SourdoughFeedStateEntity.STARVING -> lastTimeFedDt.plus(1, DateTimeUnit.DAY, TimeZone.UTC)
        }
    }
}