package app.sotchi.service

import app.sotchi.domain.exception.SourdoughNotFoundException
import app.sotchi.domain.sourdough.SourdoughEntity
import app.sotchi.dto.analytics.SourdoughCreatedEvent
import app.sotchi.dto.analytics.SourdoughDeletedEvent
import app.sotchi.dto.analytics.SourdoughFedEvent
import app.sotchi.dto.analytics.SourdoughUpdatedEvent
import app.sotchi.dto.sourdough.*
import app.sotchi.messaging.EventPublisher
import app.sotchi.repository.SourdoughRepository
import kotlin.time.Clock

class SourdoughService(
    private val sourdoughRepository: SourdoughRepository, private val eventPublisher: EventPublisher
) {
    /**
     * Business logic for: User creates a new sourdough.
     */
    fun create(dto: SourdoughCreateDTO, userId: Int): Boolean {
        val newSourdoughEntity = SourdoughEntity(
            id = dto.id,
            userId = userId,
            flourType = dto.flourType,
            liquidType = dto.liquidType,
            sourdoughName = dto.name,
            createdAtDt = Clock.System.now(),
            healthState = dto.healthState,
            lastModifiedAtDt = Clock.System.now(),
        )
        sourdoughRepository.save(newSourdoughEntity)
        eventPublisher.publish(SourdoughCreatedEvent(userId = userId, sourdoughId = dto.id))
        return true
    }

    /**
     * User modifies their sourdough
     */
    fun update(dto: SourdoughUpdateDTO, userId: Int): Boolean {
        val existingSourdough = sourdoughRepository.findById(dto.id) ?: throw SourdoughNotFoundException()

        if (dto.name != null) {
            InputValidator.validateSourdoughName(dto.name)
        }

        val updatedSourdough = existingSourdough.copy(
            sourdoughName = dto.name ?: existingSourdough.sourdoughName,
            flourType = dto.flourType ?: existingSourdough.flourType,
            liquidType = dto.liquidType ?: existingSourdough.liquidType
        )
        sourdoughRepository.save(updatedSourdough)
        eventPublisher.publish(SourdoughUpdatedEvent(userId = userId, sourdoughId = dto.id))
        return true
    }

    /**
     * Business logic for: User deletes an existing sourdough.
     */
    fun delete(dto: SourdoughDeleteDTO, userId: Int): Boolean {
        val currentSourdough = sourdoughRepository.findById(dto.id)
        eventPublisher.publish(SourdoughDeletedEvent(userId = userId, sourdoughId = dto.id))
        return sourdoughRepository.delete(currentSourdough!!.id!!)
    }

    /**
     * User feeds their sourdough
     */
    fun feed(dto: SourdoughFeedDTO, userId: Int): Boolean {
        sourdoughRepository.feed(dto.id, dto.fedAtDt)
        eventPublisher.publish(SourdoughFedEvent(userId = userId, sourdoughId = dto.id))
        return true
    }

    /**
     * User wants to see all of their sourdoughs
     */
    fun readAll(userId: Int): List<SourdoughProfileDTO> {
        val foundSourdoughs = sourdoughRepository.findAllPerUser(userId) ?: throw SourdoughNotFoundException()
        return foundSourdoughs.map { foundSourdough ->
            SourdoughProfileDTO(
                id = foundSourdough.id!!,
                flourType = foundSourdough.flourType,
                liquidType = foundSourdough.liquidType,
                sourdoughName = foundSourdough.sourdoughName,
                createdAtDt = foundSourdough.createdAtDt,
                lastModifiedAtDt = foundSourdough.lastModifiedAtDt,
                healthState = foundSourdough.healthState,
                lastFedAtDt = foundSourdough.lastFedAtDt,
            )
        }
    }

    /**
     * User wants to see one specific sourdough
     */
    fun read(dto: SourdoughReadDTO): SourdoughProfileDTO {
        val foundSourdough = sourdoughRepository.findById(dto.id) ?: throw SourdoughNotFoundException()
        return SourdoughProfileDTO(
            id = foundSourdough.id!!,
            flourType = foundSourdough.flourType,
            liquidType = foundSourdough.liquidType,
            sourdoughName = foundSourdough.sourdoughName,
            createdAtDt = foundSourdough.createdAtDt,
            lastModifiedAtDt = foundSourdough.lastModifiedAtDt,
            healthState = foundSourdough.healthState,
            lastFedAtDt = foundSourdough.lastFedAtDt,
        )
    }
}