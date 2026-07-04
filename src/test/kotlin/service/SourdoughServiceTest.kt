package service

import app.sotchi.domain.exception.SourdoughNotFoundException
import app.sotchi.domain.generic.FlourTypeEntity
import app.sotchi.domain.generic.LiquidTypeEntity
import app.sotchi.domain.generic.SourdoughHealthState
import app.sotchi.dto.sourdough.*
import app.sotchi.dto.user.UserCreateDTO
import app.sotchi.messaging.DefaultEventPublisher
import app.sotchi.messaging.EventPublisher
import app.sotchi.persistence.*
import app.sotchi.repository.SourdoughRepository
import app.sotchi.repository.SourdoughRepositoryImpl
import app.sotchi.repository.UserRepository
import app.sotchi.repository.UserRepositoryDbImpl
import app.sotchi.service.SourdoughService
import app.sotchi.service.UserService
import org.jetbrains.exposed.v1.core.DatabaseConfig
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.util.Collections
import kotlin.test.*
import kotlin.time.Clock

class SourdoughServiceTest {
    private lateinit var sourdoughService: SourdoughService
    private lateinit var sourdoughRepository: SourdoughRepository
    private lateinit var userRepository: UserRepository
    private lateinit var eventPublisher: EventPublisher
    private lateinit var userService: UserService

    /**
     * Setup which runs before each of the other tests. Inits an in-memory mock repository.
     */
    @BeforeTest
    fun setup() {
        Database.connect(
            url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
            user = "root",
            driver = "org.h2.Driver",
            password = "",
            databaseConfig = DatabaseConfig {
                defaultMaxAttempts = 3
            })

        transaction {
            SchemaUtils.drop(
                UserRoleTable,
                UserActivationTable,
                UserTable,
                SourdoughTable,
                FlourTable,
                LiquidTable,
                SourdoughFeedLogTable,
                SourdoughHealthStateTable
            )

            SchemaUtils.create(
                UserTable,
                UserRoleTable,
                UserActivationTable,
                SourdoughTable,
                FlourTable,
                LiquidTable,
                SourdoughFeedLogTable,
                SourdoughHealthStateTable
            )
        }
        userRepository = UserRepositoryDbImpl()//UserRepositoryInMemoryImpl()
        sourdoughRepository = SourdoughRepositoryImpl()
        eventPublisher = DefaultEventPublisher()
        sourdoughService = SourdoughService(sourdoughRepository, eventPublisher)
        userService = UserService(userRepository, eventPublisher)
        userService.create(
            UserCreateDTO(
                name = "test12345", email = "email@test.com", password = "test12345"
            )
        )
    }

    @Test
    fun `feed() updates feed timestamp correctly`() {
        val dto = SourdoughCreateDTO(
            name = "Test Sauerteig",
            flourType = FlourTypeEntity.WHITE_WHOLE_WHEAT,
            liquidType = LiquidTypeEntity.ORANGE_JUICE,
            healthState = SourdoughHealthState.JUST_FED
        )
        sourdoughService.create(dto, 1)
        assertNull(sourdoughRepository.findById(1)!!.lastFedAtDt)

        val fedAtDt = Clock.System.now()
        val feedDto = SourdoughFeedDTO(
            id = 1,
            fedAtDt = fedAtDt,
        )
        sourdoughService.feed(feedDto, userId = 1)
        assertNotNull(sourdoughRepository.findById(1)!!.lastFedAtDt)
        assertEquals(fedAtDt, sourdoughRepository.findById(1)!!.lastFedAtDt)
    }

    @Test
    fun `update() updates sourdough correctly`() {
        val dto = SourdoughCreateDTO(
            name = "Test Sauerteig",
            flourType = FlourTypeEntity.WHITE_WHOLE_WHEAT,
            liquidType = LiquidTypeEntity.ORANGE_JUICE,
            healthState = SourdoughHealthState.JUST_FED
        )
        sourdoughService.create(dto, 1)
        val updatedSourdough = SourdoughUpdateDTO(
            id = 1,
            flourType = FlourTypeEntity.COCONUT,
        )
        sourdoughService.update(updatedSourdough, userId = 1)

        val sourdoughAfterUpdate = sourdoughRepository.findById(1)
        assertEquals(FlourTypeEntity.COCONUT, sourdoughAfterUpdate!!.flourType)
    }

    @Test
    fun `delete() deletes sourdough correctly`() {
        val dto = SourdoughCreateDTO(
            name = "Test Sauerteig",
            flourType = FlourTypeEntity.WHITE_WHOLE_WHEAT,
            liquidType = LiquidTypeEntity.ORANGE_JUICE,
            healthState = SourdoughHealthState.JUST_FED
        )
        sourdoughService.create(dto, userId = 1)
        assertNotNull(sourdoughRepository.findById(1)!!)
        val updatedSourdough = SourdoughDeleteDTO(
            1
        )
        sourdoughService.delete(updatedSourdough, userId = 1)
        assertNull(sourdoughRepository.findById(1))
    }

    @Test
    fun `readAll() returns all the users sourdoughs`() {
        val dto = SourdoughCreateDTO(
            name = "Test Sauerteig",
            flourType = FlourTypeEntity.WHITE_WHOLE_WHEAT,
            liquidType = LiquidTypeEntity.ORANGE_JUICE,
            healthState = SourdoughHealthState.JUST_FED
        )
        sourdoughService.create(dto, userId = 1)
        val dtoTwo = SourdoughCreateDTO(
            name = "Test Sauerteig Blabber",
            flourType = FlourTypeEntity.WHITE_WHOLE_WHEAT,
            liquidType = LiquidTypeEntity.ORANGE_JUICE,
            healthState = SourdoughHealthState.JUST_FED
        )
        sourdoughService.create(dtoTwo, userId = 1)
        val dtoThree = SourdoughCreateDTO(
            name = "Test Sauerteig Blabbero",
            flourType = FlourTypeEntity.WHITE_WHOLE_WHEAT,
            liquidType = LiquidTypeEntity.ORANGE_JUICE,
            healthState = SourdoughHealthState.JUST_FED
        )
        sourdoughService.create(dtoThree, userId = 1)
        assertEquals(3, sourdoughService.readAll(1).size)
    }

    @Test
    fun `readAll() returns empty list if no sourdoughs found`() {
        assertEquals(Collections.emptyList(), sourdoughService.readAll(1))
    }

    @Test
    fun `read() returns one dedicated sourdough`() {
        val dto = SourdoughCreateDTO(
            name = "Test Sauerteig",
            flourType = FlourTypeEntity.WHITE_WHOLE_WHEAT,
            liquidType = LiquidTypeEntity.ORANGE_JUICE,
            healthState = SourdoughHealthState.JUST_FED
        )
        sourdoughService.create(dto, userId = 1)
        assertNotNull(sourdoughService.read(SourdoughReadDTO(1)))
        assertEquals(FlourTypeEntity.WHITE_WHOLE_WHEAT, sourdoughService.read(SourdoughReadDTO(1)).flourType)
    }

    @Test
    fun `read() returns exception when sourdough was not found`() {
        assertFailsWith<SourdoughNotFoundException> { sourdoughService.read(SourdoughReadDTO(1)) }
    }
}