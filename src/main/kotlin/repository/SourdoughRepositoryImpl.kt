package app.sotchi.repository

import app.sotchi.controller.resources.Sourdough
import app.sotchi.domain.sourdough.SourdoughEntity
import java.util.concurrent.atomic.AtomicInteger

class SourdoughRepositoryImpl : SourdoughRepository {
    private val sourdoughs = mutableListOf<SourdoughEntity>()
    private val removedSourdoughs = mutableListOf<SourdoughEntity>()

    override fun findAll(): List<SourdoughEntity>? {
        TODO("Not yet implemented")
    }

    override fun findById(id: Int): SourdoughEntity {
        TODO("Not yet implemented")
    }

    override fun save(sourdoughEntity: SourdoughEntity): SourdoughEntity {
        val saved = sourdoughEntity.copy()
        sourdoughs.add(saved)
        return saved
    }

    override fun delete(sourdoughEntity: SourdoughEntity): SourdoughEntity {
        val deleted = sourdoughEntity.copy()
        removedSourdoughs.add(deleted)
        return deleted
    }
}