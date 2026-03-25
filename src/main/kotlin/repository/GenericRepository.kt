package app.sotchi.repository

import app.sotchi.domain.generic.FlourTypeEntity

interface GenericRepository {
    fun findAll(): List<FlourTypeEntity>
}