package com.harbor.calendly

import com.harbor.calendly.dto.AvailabilityDTO
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Integration tests for /availability resource.
 */
class AvailabilityControllerTest: BaseTest() {
    @BeforeEach
    fun setUp() {
        availabilityRepository.deleteAll()
        accountRepository.deleteAll()
    }

    @Test
    fun testCreateAvailabilityWithInvalidDTO() {
        webTestClient
            .post()
            .uri("/accounts/1/availability")
            .bodyValue(SomeClass(123, 1234567890))
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun testCreateAvailabilityWhenEndTimeEarlierThanStartTime() {
        webTestClient
            .post()
            .uri("/accounts/1/availability")
            .bodyValue(AvailabilityDTO(DAY_1, START_TIME_1, START_TIME_1.minusHours(1)))
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun testCreateAvailability() {
        val accountId = createAccount()
        webTestClient
            .post()
            .uri("/accounts/$accountId/availability")
            .bodyValue(AvailabilityDTO(DAY_1, START_TIME_1, END_TIME_1))
            .exchange()
            .expectStatus().isCreated
            .expectBody(Int::class.java)
    }

    @Test
    fun testGetAbsentAvailability() {
        webTestClient
            .get()
            .uri("/accounts/1/availability/1")
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun testGetPresentAvailability() {
        val accountId = createAccount()
        val availabilityId = createAvailability(accountId)
        webTestClient
            .get()
            .uri("/accounts/$accountId/availability/$availabilityId")
            .exchange()
            .expectStatus().isOk
            .expectBody(AvailabilityDTO::class.java)
            .isEqualTo(AvailabilityDTO(DAY_1, START_TIME_1, END_TIME_1))
    }

    @Test
    fun testUpdateAbsentAvailability() {
        webTestClient
            .put()
            .uri("/accounts/1/availability/1")
            .bodyValue(AvailabilityDTO(DAY_1, START_TIME_1, END_TIME_1))
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun testUpdateAvailabilityWhenEndTimeEarlierThanStartTime() {
        val accountId = createAccount()
        val availabilityId = createAvailability(accountId)
        webTestClient
            .put()
            .uri("/accounts/$accountId/availability/${availabilityId}")
            .bodyValue(AvailabilityDTO(DAY_1, START_TIME_1, START_TIME_1.minusHours(1)))
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun testUpdatePresentAvailability() {
        val accountId = createAccount()
        val availabilityId = createAvailability(accountId)
        webTestClient
            .put()
            .uri("/accounts/$accountId/availability/{availabilityId}", availabilityId)
            .bodyValue(AvailabilityDTO(DAY_1, START_TIME_1, START_TIME_1.plusHours(1)))
            .exchange()
            .expectStatus().isNoContent
    }

    @Test
    fun testDeleteAbsentAvailability() {
        webTestClient
            .delete()
            .uri("/accounts/1/availability/1")
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun testDeleteAvailability() {
        val accountId = createAccount()
        val availabilityId = createAvailability(accountId)
        webTestClient
            .delete()
            .uri("/accounts/$accountId/availability/{availabilityId}", availabilityId)
            .exchange()
            .expectStatus().isNoContent
    }
}
