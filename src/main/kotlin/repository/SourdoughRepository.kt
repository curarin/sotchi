package app.sotchi.repository

import app.sotchi.domain.sourdough.SourdoughEntity


interface SourdoughRepository {
    fun findAllPerUser(userId: Int): List<SourdoughEntity>?
    fun findById(sourdoughId: Int): SourdoughEntity
    fun save(sourdoughEntity: SourdoughEntity): SourdoughEntity
    fun delete(sourdoughEntity: SourdoughEntity): SourdoughEntity
}