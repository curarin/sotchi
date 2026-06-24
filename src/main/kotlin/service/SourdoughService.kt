package app.sotchi.service

import app.sotchi.domain.exception.UserNotFoundException
import app.sotchi.domain.sourdough.SourdoughEntity
import app.sotchi.dto.sourdough.CreateSourdoughDTO
import app.sotchi.dto.sourdough.DeleteSourdoughDTO
import app.sotchi.repository.SourdoughRepository
import app.sotchi.repository.UserRepository
import kotlin.time.Clock

class SourdoughService(
    private val sourdoughRepository: SourdoughRepository,
    private val userRepository: UserRepository,
) {
    /**
     * Business logic for: User creates a new sourdough.
     */
    fun createSourdough(dto: CreateSourdoughDTO, userId: Int): SourdoughEntity {
        val foundUser = userRepository.findById(userId)
        if (foundUser != null) {
            val newSourdoughEntity = SourdoughEntity(
                user = foundUser,
                flourType = dto.flourType,
                liquidType = dto.liquidType,
                sourdoughName = dto.name,
                createdAtDt = Clock.System.now(),
                lastModifiedAtDt = Clock.System.now(),
            )
            return sourdoughRepository.save(newSourdoughEntity)
        } else {
            throw UserNotFoundException()
        }
    }

    /**
     * Business logic for: User deletes an existing sourdough.
     */
    fun deleteSourdough(dto: DeleteSourdoughDTO): SourdoughEntity {
        val currentDateTime = Clock.System.now()
        val currentSourdough = sourdoughRepository.findById(dto.id)
        return sourdoughRepository.delete(currentSourdough!!)
    }
}