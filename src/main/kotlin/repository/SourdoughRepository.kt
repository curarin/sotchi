package app.sotchi.repository

import app.sotchi.domain.sourdough.SourdoughEntity
import kotlin.time.Instant


interface SourdoughRepository {
    fun findAllPerUser(userId: Int): List<SourdoughEntity>?
    fun findById(sourdoughId: Int): SourdoughEntity?
    fun save(sourdoughEntity: SourdoughEntity): SourdoughEntity
    fun delete(sourdoughId: Int): Boolean
    fun feed(sourdoughId: Int, sourdoughFedAtDt: Instant): SourdoughEntity
}