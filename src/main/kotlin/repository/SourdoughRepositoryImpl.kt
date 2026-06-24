package app.sotchi.repository

import app.sotchi.domain.sourdough.SourdoughEntity

/**
 * Implementation of persistence layer for sourdough entity
 */
class SourdoughRepositoryImpl : SourdoughRepository {
    override fun findAllPerUser(userId: Int): List<SourdoughEntity>? {
        TODO("Not yet implemented")
    }

    override fun findById(sourdoughId: Int): SourdoughEntity {
        TODO("Not yet implemented")
    }

    override fun save(sourdoughEntity: SourdoughEntity): SourdoughEntity {
        TODO("Not yet implemented")
    }

    override fun delete(sourdoughEntity: SourdoughEntity): SourdoughEntity {
        TODO("Not yet implemented")
    }
}