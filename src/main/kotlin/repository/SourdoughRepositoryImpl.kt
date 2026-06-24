package app.sotchi.repository

import app.sotchi.domain.sourdough.SourdoughEntity
import app.sotchi.persistence.*
import org.jetbrains.exposed.v1.core.JoinType
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.*
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import kotlin.time.Clock

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
                    .where { SourdoughTable.id eq sourdoughEntity.id }
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

    override fun delete(sourdoughEntity: SourdoughEntity): SourdoughEntity {
        TODO("Not yet implemented")
    }
}