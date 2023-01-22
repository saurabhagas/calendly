package com.harbor.calendly

import com.harbor.calendly.dto.AvailabilityDTO
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
        var accountId by Delegates.notNull<Int>()
        var availabilityId1 by Delegates.notNull<Int>()
        var availabilityId2 by Delegates.notNull<Int>()
        var meetingLinkId by Delegates.notNull<Int>()
        var meetingId1 by Delegates.notNull<Int>()
        var meetingId2 by Delegates.notNull<Int>()
    }

    @Test
    fun testStep1_createAccount() {
        accountId = createAccount()
        assertTrue(accountId > 0)
    }

    @Test
    fun testStep2_createAvailabilities() {
        availabilityId1 = createAvailability(accountId)
        availabilityId2 = createAvailability2(accountId)
        assertTrue(availabilityId1 > 0)
        assertTrue(availabilityId2 > 0)
    }

    @Test
    fun testStep3_CreateMeetingLink() {
        meetingLinkId = createMeetingLink(accountId)
        assertTrue(meetingLinkId > 0)
    }

    @Test
    fun testStep4_CreateMeeting() {
        meetingId1 = createMeeting(accountId, meetingLinkId)
        meetingId2 = createMeeting2(accountId, meetingLinkId)
        assertTrue(meetingId1 > 0)
        assertTrue(meetingId2 > 0)
    }

    @Test
    fun testStep5_verifyChangeOfAvailabilityAfterMeetingCreation() {
        val allAvailabilities = getAllAvailabilities(accountId)
        println(allAvailabilities)
        // First day's availability should get split into 2, and second day's availability should get removed
        assertTrue(allAvailabilities.size == 2)
        assertTrue(allAvailabilities.contains(AvailabilityDTO(DAY_1, START_TIME_1, START_TIME_2)))
        assertTrue(allAvailabilities.contains(AvailabilityDTO(DAY_1, END_TIME_2, END_TIME_1)))
        assertFalse(allAvailabilities.contains(AvailabilityDTO(DAY_2, START_TIME_2, END_TIME_2)))
    }
}
