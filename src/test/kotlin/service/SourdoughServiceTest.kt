package service

import app.sotchi.domain.generic.FlourTypeEntity
import app.sotchi.domain.generic.LiquidTypeEntity
import app.sotchi.domain.generic.SourdoughHealthState
import app.sotchi.domain.sourdough.SourdoughEntity
import app.sotchi.dto.sourdough.SourdoughCreateDTO
import app.sotchi.dto.sourdough.SourdoughFeedDTO
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
        val newSourdoughEntity = SourdoughEntity(
            id = dto.id,
            userId = 1,
            flourType = dto.flourType,
            liquidType = dto.liquidType,
            sourdoughName = dto.name,
            createdAtDt = Clock.System.now(),
            healthState = dto.healthState,
            lastModifiedAtDt = Clock.System.now(),
        )
        sourdoughRepository.save(newSourdoughEntity)
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
}