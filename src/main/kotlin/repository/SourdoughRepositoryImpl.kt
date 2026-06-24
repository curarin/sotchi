package app.sotchi.repository

import app.sotchi.domain.sourdough.SourdoughEntity
import app.sotchi.persistence.*
import org.jetbrains.exposed.v1.core.JoinType
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.max
import org.jetbrains.exposed.v1.jdbc.*
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import kotlin.time.Clock

/**
 * Implementation of persistence layer for sourdough entity
 */
class SourdoughRepositoryImpl : SourdoughRepository {
    override fun findAllPerUser(userId: Int): List<SourdoughEntity> = transaction {
        val lastFedAtDt = SourdoughFeedLogTable.sourdoughFedAtDt.max()

        SourdoughTable.join(
            FlourTable, JoinType.INNER, additionalConstraint = { SourdoughTable.flourId eq FlourTable.id }).join(
            LiquidTable, JoinType.INNER, additionalConstraint = { SourdoughTable.liquidId eq LiquidTable.id }).join(
            SourdoughFeedLogTable, JoinType.LEFT, additionalConstraint = {
                SourdoughFeedLogTable.sourdoughId eq SourdoughTable.id
            }).select(
            SourdoughTable.columns + FlourTable.columns + LiquidTable.columns + listOf(lastFedAtDt)
        ).where { SourdoughTable.userId eq userId }.groupBy(
            SourdoughTable.id,
            SourdoughTable.userId,
            SourdoughTable.name,
            SourdoughTable.flourId,
            SourdoughTable.liquidId,
            SourdoughTable.createdAtDt,
            SourdoughTable.lastModifiedDt,
            FlourTable.id,
            FlourTable.flourType,
            LiquidTable.id,
            LiquidTable.liquidType
        ).map {
            SourdoughEntity(
                id = it[SourdoughTable.id].value,
                userId = it[SourdoughTable.userId]!!.value,
                flourType = it[FlourTable.flourType],
                liquidType = it[LiquidTable.liquidType],
                sourdoughName = it[SourdoughTable.name],
                createdAtDt = it[SourdoughTable.createdAtDt],
                lastModifiedAtDt = it[SourdoughTable.lastModifiedDt],
                lastFedAtDt = it[lastFedAtDt]
            )
        }
    }

    override fun findById(sourdoughId: Int): SourdoughEntity? = transaction {
        val lastFedAtDt = SourdoughFeedLogTable.sourdoughFedAtDt.max()
        SourdoughTable.join(
            FlourTable, JoinType.INNER, additionalConstraint = { SourdoughTable.flourId eq FlourTable.id }).join(
            LiquidTable, JoinType.INNER, additionalConstraint = { SourdoughTable.liquidId eq LiquidTable.id }).join(
            SourdoughFeedLogTable, JoinType.LEFT, additionalConstraint = {
                SourdoughFeedLogTable.sourdoughId eq SourdoughTable.id
            }).select(
            SourdoughTable.columns + FlourTable.columns + LiquidTable.columns + listOf(lastFedAtDt)
        ).where { SourdoughTable.id eq sourdoughId }.groupBy(
            SourdoughTable.id,
            SourdoughTable.userId,
            SourdoughTable.name,
            SourdoughTable.flourId,
            SourdoughTable.liquidId,
            SourdoughTable.createdAtDt,
            SourdoughTable.lastModifiedDt,
            FlourTable.id,
            FlourTable.flourType,
            LiquidTable.id,
            LiquidTable.liquidType
        ).map {
            SourdoughEntity(
                id = it[SourdoughTable.id].value,
                userId = it[SourdoughTable.userId]!!.value,
                flourType = it[FlourTable.flourType],
                liquidType = it[LiquidTable.liquidType],
                sourdoughName = it[SourdoughTable.name],
                createdAtDt = it[SourdoughTable.createdAtDt],
                lastModifiedAtDt = it[SourdoughTable.lastModifiedDt],
                lastFedAtDt = it[lastFedAtDt]
            )
        }.singleOrNull()
    }

    override fun save(sourdoughEntity: SourdoughEntity): SourdoughEntity {
        val saveRow = transaction {
            // If id is 0 we create a new one
            if (sourdoughEntity.id == 0) {

                val liquidTypeInserted = LiquidTable.select(LiquidTable.id).where {
                    LiquidTable.liquidType eq sourdoughEntity.liquidType
                }.singleOrNull()?.get(LiquidTable.id) ?: LiquidTable.insertAndGetId {
                    it[liquidType] = sourdoughEntity.liquidType
                }

                val flourTypeInserted = FlourTable.select(FlourTable.id).where {
                    FlourTable.flourType eq sourdoughEntity.flourType
                }.singleOrNull()?.get(FlourTable.id) ?: FlourTable.insertAndGetId {
                    it[flourType] = sourdoughEntity.flourType
                }

                val sourdoughInserted = SourdoughTable.insert {
                    it[name] = sourdoughEntity.sourdoughName
                    it[userId] = sourdoughEntity.userId
                    it[liquidId] = liquidTypeInserted
                    it[flourId] = flourTypeInserted
                    it[createdAtDt] = sourdoughEntity.createdAtDt
                    it[lastModifiedDt] = Clock.System.now()
                }

                val generatedId = sourdoughInserted[SourdoughTable.id].value

                LOGGER.info("New sourdough inserted with id: $generatedId")

                SourdoughTable.join(
                    FlourTable, JoinType.INNER, additionalConstraint = { SourdoughTable.flourId eq FlourTable.id })
                    .join(
                        LiquidTable,
                        JoinType.INNER,
                        additionalConstraint = { SourdoughTable.liquidId eq LiquidTable.id })
                    .join(UserTable, JoinType.INNER, additionalConstraint = { SourdoughTable.userId eq UserTable.id })
                    .join(
                        SourdoughFeedLogTable,
                        JoinType.LEFT,
                        additionalConstraint = { SourdoughFeedLogTable.sourdoughId eq SourdoughTable.id }).selectAll()
                    .where { SourdoughTable.id eq generatedId }
                    .orderBy(SourdoughFeedLogTable.sourdoughFedAtDt, SortOrder.DESC).limit(1).single()
            } else {
                // If the sourdough per se already exists we just update stuff
                val flourTypeInserted =
                    FlourTable.select(FlourTable.id).where { FlourTable.flourType eq sourdoughEntity.flourType }
                        .singleOrNull()?.get(FlourTable.id) ?: FlourTable.insertAndGetId {
                        it[flourType] = sourdoughEntity.flourType
                    }

                val liquidTypeInserted =
                    LiquidTable.select(LiquidTable.id).where { LiquidTable.liquidType eq sourdoughEntity.liquidType }
                        .singleOrNull()?.get(LiquidTable.id) ?: LiquidTable.insertAndGetId {
                        it[liquidType] = sourdoughEntity.liquidType
                    }

                SourdoughTable.update({ SourdoughTable.id eq sourdoughEntity.id }) {
                    it[name] = sourdoughEntity.sourdoughName
                    it[userId] = sourdoughEntity.userId
                    it[flourId] = flourTypeInserted
                    it[liquidId] = liquidTypeInserted
                    it[lastModifiedDt] = Clock.System.now()
                    it[createdAtDt] = sourdoughEntity.createdAtDt
                }
                LOGGER.info("Existing sourdough with id $sourdoughEntity.id updated")

                SourdoughTable.join(
                    FlourTable, JoinType.INNER, additionalConstraint = { SourdoughTable.flourId eq FlourTable.id })
                    .join(
                        LiquidTable,
                        JoinType.INNER,
                        additionalConstraint = { SourdoughTable.liquidId eq LiquidTable.id })
                    .join(UserTable, JoinType.INNER, additionalConstraint = { SourdoughTable.userId eq UserTable.id })
                    .join(
                        SourdoughFeedLogTable,
                        JoinType.LEFT,
                        additionalConstraint = { SourdoughFeedLogTable.sourdoughId eq SourdoughTable.id }).selectAll()
                    .where { SourdoughTable.id eq sourdoughEntity.id }
                    .orderBy(SourdoughFeedLogTable.sourdoughFedAtDt, SortOrder.DESC).limit(1).single()
            }
        }
        return SourdoughEntity(
            id = saveRow[SourdoughTable.id].value,
            userId = saveRow[UserTable.id].value,
            flourType = saveRow[FlourTable.flourType],
            liquidType = saveRow[LiquidTable.liquidType],
            sourdoughName = saveRow[SourdoughTable.name],
            createdAtDt = saveRow[SourdoughTable.createdAtDt],
            lastModifiedAtDt = saveRow[SourdoughTable.lastModifiedDt],
            lastFedAtDt = saveRow[SourdoughFeedLogTable.sourdoughFedAtDt]
        )
    }

    override fun delete(sourdoughId: Int): Boolean {
        val deletedCount = transaction {
            SourdoughTable.deleteWhere { SourdoughTable.id eq sourdoughId }
        }
        if (deletedCount > 0) {
            LOGGER.info("Sourdough with id $sourdoughId deleted")
            return true
        } else {
            LOGGER.warn("Tried to delete sourdough with id $sourdoughId - no rows deleted")
            return false
        }
    }

    override fun feed(sourdoughId: Int): SourdoughEntity = transaction {
        val currentTimestamp = Clock.System.now()

        SourdoughFeedLogTable.insert {
            it[this.sourdoughId] = EntityID(sourdoughId, SourdoughTable)
            it[sourdoughFedAtDt] = currentTimestamp
        }

        SourdoughTable.update({ SourdoughTable.id eq sourdoughId }) {
            it[lastModifiedDt] = currentTimestamp
        }

        findById(sourdoughId) as SourdoughEntity
    }
}