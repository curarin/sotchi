package app.sotchi.service

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
        eventPublisher.publish(SourdoughUpdatedEvent(userId = userId, sourdoughId = dto.id))
        TODO("Not yet implemented")
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
    fun readAll(userId: Int): List<SourdoughEntity> {
        TODO("Not yet implemented")
    }

    /**
     * User wants to see one specific sourdough
     */
    fun read(dto: SourdoughReadDTO, userId: Int): SourdoughEntity {
        TODO("Not yet implemented")
    }
}