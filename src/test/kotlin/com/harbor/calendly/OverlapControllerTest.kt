package com.harbor.calendly

import com.harbor.calendly.dto.AvailabilityDTO
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Integration tests for /accounts/{hostAccountId}/overlap resource.
 */
class OverlapControllerTest : BaseTest() {
    @BeforeEach
    fun setUp() {
        availabilityRepository.deleteAll()
        accountRepository.deleteAll()
    }

    @Test
    fun testGetOverlapsWhenNoAvailabilitiesExist() {
        val hostAccountId = createAccount1()
        val requesterAccountId = createAccount2()
        assertEquals(0, getOverlappingAvailabilities(hostAccountId, requesterAccountId).size)
    }

    @Test
    fun testGetOverlapsWhenOneSetOfAvailabilitiesExist() {
        val hostAccountId = createAccount1()
        createAvailability(hostAccountId)
        val requesterAccountId = createAccount2()
        assertEquals(0, getOverlappingAvailabilities(hostAccountId, requesterAccountId).size)
    }

    @Test
    fun testGetOverlapsWhenAvailabilitiesExistButDontOverlap() {
        val hostAccountId = createAccount1()
        createAvailability(hostAccountId)
        val requesterAccountId = createAccount2()
        createAvailability2(requesterAccountId)
        assertEquals(0, getOverlappingAvailabilities(hostAccountId, requesterAccountId).size)
    }

    @Test
    fun testGetOverlapsWhenAvailabilitiesExistAndPartiallyOverlap() {
        val hostAccountId = createAccount1()
        createAvailability(hostAccountId)
        val requesterAccountId = createAccount2()
        createAvailability(requesterAccountId, startTime = START_TIME_2)

        val overlappingAvailabilities = getOverlappingAvailabilities(hostAccountId, requesterAccountId)
        assertEquals(1, overlappingAvailabilities.size)
        assertEquals(AvailabilityDTO(DAY_1, START_TIME_2, END_TIME_1), overlappingAvailabilities.first())
    }

    @Test
    fun testGetOverlapsWhenAvailabilitiesExistAndFullyOverlap() {
        val hostAccountId = createAccount1()
        createAvailability(hostAccountId)
        val requesterAccountId = createAccount2()
        createAvailability(requesterAccountId)

        val overlappingAvailabilities = getOverlappingAvailabilities(hostAccountId, requesterAccountId)
        assertEquals(1, overlappingAvailabilities.size)
        assertEquals(AvailabilityDTO(DAY_1, START_TIME_1, END_TIME_1), overlappingAvailabilities.first())
    }
}
