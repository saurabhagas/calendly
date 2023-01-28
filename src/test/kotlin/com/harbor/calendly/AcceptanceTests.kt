package com.harbor.calendly

import com.harbor.calendly.dto.AvailabilityDTO
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import kotlin.properties.Delegates

/**
 * Acceptance tests for end-to-end functionality of the application.
 * NOTE: Each test is dependent on the previous test for correct state creation and WILL NOT run in isolation.
 */
@TestMethodOrder(value = MethodOrderer.MethodName::class)
class AcceptanceTests : BaseTest() {
    companion object {
        var hostAccountId by Delegates.notNull<Int>()
        var requesterAccountId by Delegates.notNull<Int>()
        var meetingLinkId by Delegates.notNull<Int>()
    }

    @Test
    fun testStep1_createAccounts() {
        println("#####Running testStep1_createAccounts")
        hostAccountId = createAccount1()
        assertTrue(hostAccountId > 0)
        requesterAccountId = createAccount2()
        assertTrue(requesterAccountId > 0)
        assertTrue(requesterAccountId != hostAccountId)
    }

    @Test
    fun testStep2_createAvailabilities() {
        println("#####Running testStep2_createAvailabilities")
        assertTrue(createAvailability(hostAccountId) > 0)
        assertTrue(createAvailability2(hostAccountId) > 0)
        assertTrue(createAvailability(requesterAccountId, startTime = START_TIME_2) > 0)
        assertTrue(createAvailability2(requesterAccountId) > 0)
    }

    @Test
    fun testStep3_CreateMeetingLink() {
        println("#####Running testStep3_CreateMeetingLink")
        meetingLinkId = createMeetingLink(hostAccountId)
        assertTrue(meetingLinkId > 0)
    }

    @Test
    fun testStep4_findOverlappingAvailabilities() {
        println("#####Running testStep4_findOverlappingAvailabilities")
        val overlappingAvailabilities = getOverlappingAvailabilities(hostAccountId, requesterAccountId)
        assertEquals(2, overlappingAvailabilities.size)
        assertTrue(overlappingAvailabilities.contains(AvailabilityDTO(DAY_1, START_TIME_2, END_TIME_1)))
        assertTrue(overlappingAvailabilities.contains(AvailabilityDTO(DAY_2, START_TIME_2, END_TIME_2)))
    }

    @Test
    fun testStep5_CreateMeeting() {
        println("#####Running testStep5_CreateMeeting")
        assertTrue(createMeeting(hostAccountId, requesterAccountId, meetingLinkId) > 0)
        assertTrue(createMeeting2(hostAccountId, requesterAccountId, meetingLinkId) > 0)
    }

    @Test
    fun testStep6_verifyChangeOfAvailabilitiesAfterMeetingCreation() {
        println("#####Running testStep6_verifyChangeOfAvailabilitiesAfterMeetingCreation")
        val hostAvailabilities = getAllAvailabilities(hostAccountId)
        // First day's availability should get split into 2, and second day's availability should get removed
        assertEquals(2, hostAvailabilities.size)
        assertTrue(hostAvailabilities.contains(AvailabilityDTO(DAY_1, START_TIME_1, START_TIME_2)))
        assertTrue(hostAvailabilities.contains(AvailabilityDTO(DAY_1, END_TIME_2, END_TIME_1)))
        assertFalse(hostAvailabilities.contains(AvailabilityDTO(DAY_2, START_TIME_2, END_TIME_2)))

        val requesterAvailabilities = getAllAvailabilities(requesterAccountId)
        // First day's availability should change, and second day's availability should get removed
        assertEquals(1, requesterAvailabilities.size)
        assertTrue(requesterAvailabilities.contains(AvailabilityDTO(DAY_1, END_TIME_2, END_TIME_1)))
        assertFalse(requesterAvailabilities.contains(AvailabilityDTO(DAY_2, START_TIME_2, END_TIME_2)))
    }
}
