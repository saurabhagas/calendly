package com.harbor.calendly

import com.harbor.calendly.dto.AccountDTO
import com.harbor.calendly.dto.AvailabilityDTO
import com.harbor.calendly.repository.AccountRepository
import com.harbor.calendly.repository.AvailabilityRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.DayOfWeek
import java.time.LocalTime


/**
 * Integration tests for /availability resource.
 * Serves the following purposes:
 * 1. Helps with CI of new changes
 * 2. Documentation for the APIs on /availability resource
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
internal class AvailabilityControllerTest {

    companion object {
        private val DAY_1 = DayOfWeek.MONDAY
        private val START_TIME_1 = LocalTime.of(9, 30)
        private val END_TIME_1 = LocalTime.of(17, 30)
    }

    @Autowired
    lateinit var webTestClient: WebTestClient

    @Autowired
    lateinit var accountRepository: AccountRepository

    @Autowired
    lateinit var availabilityRepository: AvailabilityRepository

    @BeforeEach
    fun setUp() {
        availabilityRepository.deleteAll()
        accountRepository.deleteAll()
    }

    @Test
    fun testCreateAvailabilityWithInvalidDTO() {
        webTestClient
            .post()
            .uri("/availability")
            .bodyValue(SomeClass(123, 1234567890))
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun testCreateAvailabilityWhenEndTimeEarlierThanStartTime() {
        webTestClient
            .post()
            .uri("/availability")
            .bodyValue(AvailabilityDTO(1, DAY_1, START_TIME_1, START_TIME_1.minusHours(1)))
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun testCreateAvailability() {
        val accountId = createAccount()
        webTestClient
            .post()
            .uri("/availability")
            .bodyValue(AvailabilityDTO(accountId, DAY_1, START_TIME_1, END_TIME_1))
            .exchange()
            .expectStatus().isCreated
            .expectBody(Int::class.java)
    }

    @Test
    fun testGetAbsentAvailability() {
        webTestClient
            .get()
            .uri("/availability/1")
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun testGetPresentAvailability() {
        val (accountId, availabilityId) = createAvailability()

        webTestClient
            .get()
            .uri("/availability/{availabilityId}", availabilityId)
            .exchange()
            .expectStatus().isOk
            .expectBody(AvailabilityDTO::class.java)
            .isEqualTo(AvailabilityDTO(accountId, DAY_1, START_TIME_1, END_TIME_1))
    }

    @Test
    fun testUpdateAbsentAvailability() {
        webTestClient
            .put()
            .uri("/availability/1")
            .bodyValue(AvailabilityDTO(1, DAY_1, START_TIME_1, END_TIME_1))
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun testUpdateAvailabilityWhenEndTimeEarlierThanStartTime() {
        val (accountId, availabilityId) = createAvailability()
        webTestClient
            .put()
            .uri("/availability/{availabilityId}", availabilityId)
            .bodyValue(AvailabilityDTO(accountId, DAY_1, START_TIME_1, START_TIME_1.minusHours(1)))
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun testUpdatePresentAvailability() {
        val (accountId, availabilityId) = createAvailability()
        webTestClient
            .put()
            .uri("/availability/{availabilityId}", availabilityId)
            .bodyValue(AvailabilityDTO(accountId, DAY_1, START_TIME_1, START_TIME_1.plusHours(1)))
            .exchange()
            .expectStatus().isNoContent
    }

    @Test
    fun testDeleteAbsentAvailability() {
        webTestClient
            .delete()
            .uri("/availability/1")
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun testDeleteAvailability() {
        val (_, availabilityId) = createAvailability()
        webTestClient
            .delete()
            .uri("/availability/{availabilityId}", availabilityId)
            .exchange()
            .expectStatus().isNoContent
    }

    private fun createAvailability(): Pair<Int, Int> {
        val accountId = createAccount()
        val availabilityId = webTestClient
            .post()
            .uri("/availability")
            .bodyValue(AvailabilityDTO(accountId, DAY_1, START_TIME_1, END_TIME_1))
            .exchange()
            .expectBody(Int::class.java)
            .returnResult()
            .responseBody!!
        return accountId to availabilityId
    }

    private fun createAccount() = webTestClient
        .post()
        .uri("/accounts")
        .bodyValue(AccountDTO("Saurabh Agarwal", "saurabh.agarwal@somedomain.com", "123-234-345"))
        .exchange()
        .expectBody(Int::class.java)
        .returnResult()
        .responseBody!!
}