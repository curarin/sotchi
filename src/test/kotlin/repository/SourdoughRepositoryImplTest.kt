package repository

import app.sotchi.domain.generic.FlourTypeEntity
import app.sotchi.domain.generic.LiquidTypeEntity
import app.sotchi.domain.sourdough.SourdoughEntity
import app.sotchi.dto.sourdough.ModifySourdoughDTO
import app.sotchi.dto.user.UserCreateDTO
import app.sotchi.messaging.DefaultEventPublisher
import app.sotchi.messaging.EventPublisher
import app.sotchi.persistence.UserActivationTable
import app.sotchi.persistence.UserRoleTable
import app.sotchi.persistence.UserTable
import app.sotchi.repository.SourdoughRepository
import app.sotchi.repository.SourdoughRepositoryImpl
import app.sotchi.repository.UserRepository
import app.sotchi.repository.UserRepositoryDbImpl
import app.sotchi.service.UserService
import org.jetbrains.exposed.v1.core.DatabaseConfig
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.time.Clock

class SourdoughRepositoryImplTest {
    private lateinit var sourdoughRepository: SourdoughRepository
    private lateinit var userService: UserService
    private lateinit var userRepository: UserRepository
    private lateinit var eventPublisher: EventPublisher

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
            SchemaUtils.drop(UserRoleTable, UserActivationTable, UserTable)

            SchemaUtils.create(UserTable, UserRoleTable, UserActivationTable)
        }
        userRepository = UserRepositoryDbImpl()//UserRepositoryInMemoryImpl()
        sourdoughRepository = SourdoughRepositoryImpl()
        eventPublisher = DefaultEventPublisher()
        userService = UserService(userRepository, eventPublisher)
        userService.create(
            UserCreateDTO(
                name = "test12345", email = "email@test.com", password = "test12345"
            )
        )
    }

    @Test
    fun `findById() finds the sourdough`() {
        val newSourdough = SourdoughEntity(
            user = userRepository.findById(1)!!,
            flourType = FlourTypeEntity.RYE,
            liquidType = LiquidTypeEntity.WATER,
            sourdoughName = "Testteig",
            createdAtDt = Clock.System.now(),
            lastModifiedAtDt = Clock.System.now()
        )
        sourdoughRepository.save(newSourdough)
        assertNotNull(sourdoughRepository.findById(1))
    }

    @Test
    fun `findAllPerUser() returns all sourdoughs for single user`() {
        val newSourdough = SourdoughEntity(
            user = userRepository.findById(1)!!,
            flourType = FlourTypeEntity.RYE,
            liquidType = LiquidTypeEntity.WATER,
            sourdoughName = "Testteig",
            createdAtDt = Clock.System.now(),
            lastModifiedAtDt = Clock.System.now()
        )
        sourdoughRepository.save(newSourdough)

        val newSourdoughTwo = SourdoughEntity(
            user = userRepository.findById(1)!!,
            flourType = FlourTypeEntity.WHITE_WHOLE_WHEAT,
            liquidType = LiquidTypeEntity.ORANGE_JUICE,
            sourdoughName = "Testteig",
            createdAtDt = Clock.System.now(),
            lastModifiedAtDt = Clock.System.now()
        )
        sourdoughRepository.save(newSourdoughTwo)
        assertNotNull(sourdoughRepository.findAllPerUser(1))
        assertEquals(2, sourdoughRepository.findAllPerUser(1)!!.size)
    }

    @Test
    fun `save() correctly inserts a new sourdough`() {
        val newSourdough = SourdoughEntity(
            user = userRepository.findById(1)!!,
            flourType = FlourTypeEntity.RYE,
            liquidType = LiquidTypeEntity.WATER,
            sourdoughName = "Testteig",
            createdAtDt = Clock.System.now(),
            lastModifiedAtDt = Clock.System.now()
        )
        sourdoughRepository.save(newSourdough)
        assertNotNull(sourdoughRepository.findAllPerUser(1))
        assertEquals(newSourdough.sourdoughName, sourdoughRepository.findAllPerUser(1)!![0].sourdoughName)
    }

    @Test
    fun `save() correctly updates an existing sourdough`() {
        val newSourdough = SourdoughEntity(
            user = userRepository.findById(1)!!,
            flourType = FlourTypeEntity.RYE,
            liquidType = LiquidTypeEntity.WATER,
            sourdoughName = "Testteig",
            createdAtDt = Clock.System.now(),
            lastModifiedAtDt = Clock.System.now()
        )
        sourdoughRepository.save(newSourdough)
        val existingSourdough = sourdoughRepository.findById(1)
        val dto = ModifySourdoughDTO(
            id = existingSourdough!!.id!!,
            flourType = FlourTypeEntity.COCONUT
        )
        val updatedSourdough = existingSourdough.copy(
            id = existingSourdough.id,
            user = userRepository.findById(1)!!,
            flourType = dto.flourType ?: existingSourdough.flourType,
            liquidType = dto.liquidType ?: existingSourdough.liquidType,
            sourdoughName = dto.name ?: existingSourdough.sourdoughName,
            createdAtDt = existingSourdough.createdAtDt,
            lastFedAtDt = existingSourdough.lastFedAtDt,
            lastModifiedAtDt = Clock.System.now()
        )
        sourdoughRepository.save(updatedSourdough)

        val sourdoughAfterUpdate = sourdoughRepository.findById(1)
        assertNotNull(sourdoughRepository.findById(1))
        assertEquals(sourdoughAfterUpdate!!.flourType, FlourTypeEntity.COCONUT)
    }

    @Test
    fun `delete() deletes a sourdough`() {
        val newSourdough = SourdoughEntity(
            user = userRepository.findById(1)!!,
            flourType = FlourTypeEntity.RYE,
            liquidType = LiquidTypeEntity.WATER,
            sourdoughName = "Testteig",
            createdAtDt = Clock.System.now(),
            lastModifiedAtDt = Clock.System.now()
        )
        sourdoughRepository.save(newSourdough)
        val existingSourdough = sourdoughRepository.findById(1)
        assertNotNull(existingSourdough)
        sourdoughRepository.delete(existingSourdough)
        assertNull(sourdoughRepository.findById(1))
    }
}