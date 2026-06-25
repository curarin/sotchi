package service

import app.sotchi.domain.generic.FlourTypeEntity
import app.sotchi.domain.generic.LiquidTypeEntity
import app.sotchi.domain.generic.SourdoughHealthState
import app.sotchi.domain.sourdough.SourdoughEntity
import app.sotchi.service.SourdoughHealthStateMachine
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days

/**
 * Tests the current implementation of the state machine for
 * sourdough health
 */
class SourdoughHealthStateMachineTest {



    @Test
    fun `state changes to JUST_FED after feeding`() {
        val newSourdough = SourdoughEntity(
            id = 1,
            userId = 1,
            flourType = FlourTypeEntity.WHITE_WHOLE_WHEAT,
            liquidType = LiquidTypeEntity.WATER,
            sourdoughName = "Saubärteig",
            createdAtDt = Clock.System.now(),
            lastModifiedAtDt = Clock.System.now(),
            lastFedAtDt = Clock.System.now(),
            healthState = SourdoughHealthState.UNDEFINED
        )

        val state = SourdoughHealthStateMachine(newSourdough).traverse()
        assertEquals(SourdoughHealthState.JUST_FED, state)
    }

    @Test
    fun `state is still JUST_FED after 3 days since lastTimeFed`() {
        val newSourdough = SourdoughEntity(
            id = 1,
            userId = 1,
            flourType = FlourTypeEntity.WHITE_WHOLE_WHEAT,
            liquidType = LiquidTypeEntity.WATER,
            sourdoughName = "Saubärteig",
            createdAtDt = Clock.System.now(),
            lastModifiedAtDt = Clock.System.now(),
            lastFedAtDt = Clock.System.now() - 3.days,
            healthState = SourdoughHealthState.JUST_FED
        )

        val state = SourdoughHealthStateMachine(newSourdough).traverse()
        assertEquals(SourdoughHealthState.JUST_FED, state)
    }

    @Test
    fun `state changes to HUNGRY after 4 days since lastTimeFed`() {
        val newSourdough = SourdoughEntity(
            id = 1,
            userId = 1,
            flourType = FlourTypeEntity.WHITE_WHOLE_WHEAT,
            liquidType = LiquidTypeEntity.WATER,
            sourdoughName = "Saubärteig",
            createdAtDt = Clock.System.now(),
            lastModifiedAtDt = Clock.System.now(),
            lastFedAtDt = Clock.System.now() - 4.days,
            healthState = SourdoughHealthState.JUST_FED
        )

        val state = SourdoughHealthStateMachine(newSourdough).traverse()
        assertEquals(SourdoughHealthState.HUNGRY, state)
    }

    @Test
    fun `state still HUNGRY after 7 days since lastTimeFed`() {
        val newSourdough = SourdoughEntity(
            id = 1,
            userId = 1,
            flourType = FlourTypeEntity.WHITE_WHOLE_WHEAT,
            liquidType = LiquidTypeEntity.WATER,
            sourdoughName = "Saubärteig",
            createdAtDt = Clock.System.now(),
            lastModifiedAtDt = Clock.System.now(),
            lastFedAtDt = Clock.System.now() - 7.days,
            healthState = SourdoughHealthState.HUNGRY
        )

        val state = SourdoughHealthStateMachine(newSourdough).traverse()
        assertEquals(SourdoughHealthState.HUNGRY, state)
    }

    @Test
    fun `state changes to WEAK after 8 days since lastTimeFed`() {
        val newSourdough = SourdoughEntity(
            id = 1,
            userId = 1,
            flourType = FlourTypeEntity.WHITE_WHOLE_WHEAT,
            liquidType = LiquidTypeEntity.WATER,
            sourdoughName = "Saubärteig",
            createdAtDt = Clock.System.now(),
            lastModifiedAtDt = Clock.System.now(),
            lastFedAtDt = Clock.System.now() - 8.days,
            healthState = SourdoughHealthState.HUNGRY
        )

        val state = SourdoughHealthStateMachine(newSourdough).traverse()
        assertEquals(SourdoughHealthState.WEAK, state)
    }

    @Test
    fun `state still WEAK after 9 days since lastTimeFed`() {
        val newSourdough = SourdoughEntity(
            id = 1,
            userId = 1,
            flourType = FlourTypeEntity.WHITE_WHOLE_WHEAT,
            liquidType = LiquidTypeEntity.WATER,
            sourdoughName = "Saubärteig",
            createdAtDt = Clock.System.now(),
            lastModifiedAtDt = Clock.System.now(),
            lastFedAtDt = Clock.System.now() - 9.days,
            healthState = SourdoughHealthState.WEAK
        )

        val state = SourdoughHealthStateMachine(newSourdough).traverse()
        assertEquals(SourdoughHealthState.WEAK, state)
    }

    @Test
    fun `state changes to TOXIC after 10 days since lastTimeFed`() {
        val newSourdough = SourdoughEntity(
            id = 1,
            userId = 1,
            flourType = FlourTypeEntity.WHITE_WHOLE_WHEAT,
            liquidType = LiquidTypeEntity.WATER,
            sourdoughName = "Saubärteig",
            createdAtDt = Clock.System.now(),
            lastModifiedAtDt = Clock.System.now(),
            lastFedAtDt = Clock.System.now() - 10.days,
            healthState = SourdoughHealthState.WEAK
        )

        val state = SourdoughHealthStateMachine(newSourdough).traverse()
        assertEquals(SourdoughHealthState.TOXIC, state)
    }

    @Test
    fun `state still TOXIC after 14 days since lastTimeFed`() {
        val newSourdough = SourdoughEntity(
            id = 1,
            userId = 1,
            flourType = FlourTypeEntity.WHITE_WHOLE_WHEAT,
            liquidType = LiquidTypeEntity.WATER,
            sourdoughName = "Saubärteig",
            createdAtDt = Clock.System.now(),
            lastModifiedAtDt = Clock.System.now(),
            lastFedAtDt = Clock.System.now() - 14.days,
            healthState = SourdoughHealthState.TOXIC
        )

        val state = SourdoughHealthStateMachine(newSourdough).traverse()
        assertEquals(SourdoughHealthState.TOXIC, state)
    }

    @Test
    fun `state changes to DEAD after 15 days since lastTimeFed`() {
        val newSourdough = SourdoughEntity(
            id = 1,
            userId = 1,
            flourType = FlourTypeEntity.WHITE_WHOLE_WHEAT,
            liquidType = LiquidTypeEntity.WATER,
            sourdoughName = "Saubärteig",
            createdAtDt = Clock.System.now(),
            lastModifiedAtDt = Clock.System.now(),
            lastFedAtDt = Clock.System.now() - 15.days,
            healthState = SourdoughHealthState.TOXIC
        )

        val state = SourdoughHealthStateMachine(newSourdough).traverse()
        assertEquals(SourdoughHealthState.DEAD, state)
    }
}