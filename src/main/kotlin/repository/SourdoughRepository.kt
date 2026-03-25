package app.sotchi.repository

import app.sotchi.domain.sourdough.SourdoughEntity


interface SourdoughRepository {
    fun findAll(): List<SourdoughEntity>?
    fun findById(id: Int): SourdoughEntity
    fun save(sourdoughEntity: SourdoughEntity): SourdoughEntity
    fun delete(sourdoughEntity: SourdoughEntity): SourdoughEntity
}