package com.harbor.calendly

import com.harbor.calendly.dto.AvailabilityDTO
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
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
        val accountId = createAccount1()
        webTestClient
            .post()
            .uri("/accounts/$accountId/availability")
            .bodyValue(AvailabilityDTO(DAY_1, START_TIME_1, END_TIME_1))
            .exchange()
            .expectStatus().isCreated
            .expectBody(Int::class.java)
    }

    @Test
    fun testNoMergeAvailabilitiesWhenIntervalsAreDisjoint() {
        val accountId = createAccount1()
        createAvailability(accountId)
        createAvailability2(accountId)
        assertEquals(2, getAllAvailabilities(accountId).size)
    }

    @Test
    fun testMergeAvailabilitiesWhenIntervalsAreContiguous() {
        val accountId = createAccount1()
        createAvailability(accountId)
        val endTime = END_TIME_1.plusHours(1)
        createAvailability2(accountId, dayOfWeek = DAY_1, startTime = END_TIME_1, endTime = endTime)

        val allAvailabilities = getAllAvailabilities(accountId)
        assertEquals(1, allAvailabilities.size)
        assertTrue(allAvailabilities.contains(AvailabilityDTO(DAY_1, START_TIME_1, endTime)))
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
        val accountId = createAccount1()
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
        val accountId = createAccount1()
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
        val accountId = createAccount1()
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
        val accountId = createAccount1()
        val availabilityId = createAvailability(accountId)
        webTestClient
            .delete()
            .uri("/accounts/$accountId/availability/{availabilityId}", availabilityId)
            .exchange()
            .expectStatus().isNoContent
    }
}
