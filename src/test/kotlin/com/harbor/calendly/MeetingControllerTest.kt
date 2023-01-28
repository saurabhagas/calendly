package com.harbor.calendly

import com.harbor.calendly.dto.MeetingDTO
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Integration tests for /meetings resource.
 */
class MeetingControllerTest:BaseTest() {
    @BeforeEach
    fun setUp() {
        meetingRepository.deleteAll()
        meetingLinkRepository.deleteAll()
        availabilityRepository.deleteAll()
        accountRepository.deleteAll()
    }

    @Test
    fun testCreateMeetingWithInvalidDTO() {
        webTestClient
            .post()
            .uri("/accounts/1/meeting-links/1/meetings")
            .bodyValue(SomeClass(123, 1234567890))
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun testCreateMeetingWhenEndTimeEarlierThanStartTime() {
        webTestClient
            .post()
            .uri("/accounts/1/meeting-links/1/meetings")
            .bodyValue(MeetingDTO(DATE_1, START_TIME_1, START_TIME_1.minusHours(1), 1))
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun testCreateMeeting() {
        val hostAccountId = createAccount1()
        val requesterAccountId = createAccount2()
        createAvailability(hostAccountId)
        createAvailability2(hostAccountId)
        createAvailability(requesterAccountId)
        createAvailability2(requesterAccountId)
        val meetingLinkId = createMeetingLink(hostAccountId)
        webTestClient
            .post()
            .uri("/accounts/$hostAccountId/meeting-links/$meetingLinkId/meetings")
            .bodyValue(newMeetingDTO(requesterAccountId))
            .exchange()
            .expectStatus().isCreated
            .expectBody(Int::class.java)
    }

    @Test
    fun testGetAbsentMeeting() {
        webTestClient
            .get()
            .uri("/accounts/1/meeting-links/1/meetings/1")
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun testGetPresentMeeting() {
        val hostAccountId = createAccount1() // 7
        val requesterAccountId = createAccount2() // 8
        createAvailability(hostAccountId)
        createAvailability2(hostAccountId)
        createAvailability(requesterAccountId)
        createAvailability2(requesterAccountId)
        val meetingLinkId = createMeetingLink(hostAccountId)
        val meetingId = createMeeting(hostAccountId, requesterAccountId, meetingLinkId)
        webTestClient
            .get()
            .uri("/accounts/$hostAccountId/meeting-links/$meetingLinkId/meetings/$meetingId")
            .exchange()
            .expectStatus().isOk
            .expectBody(MeetingDTO::class.java)
            .isEqualTo(newMeetingDTO(requesterAccountId))
    }

    @Test
    fun testUpdateAbsentMeeting() {
        webTestClient
            .put()
            .uri("/accounts/1/meeting-links/1/meetings/1")
            .bodyValue(newMeetingDTO(1))
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun testUpdateMeetingWhenEndTimeEarlierThanStartTime() {
        val accountId = createAccount1()
        val requesterAccountId = createAccount2()
        createAvailability(accountId)
        createAvailability2(accountId)
        createAvailability(requesterAccountId)
        createAvailability2(requesterAccountId)
        val meetingLinkId = createMeetingLink(accountId)
        val meetingId = createMeeting(accountId, requesterAccountId, meetingLinkId)
        webTestClient
            .put()
            .uri("/accounts/$accountId/meeting-links/$meetingLinkId/meetings/$meetingId")
            .bodyValue(MeetingDTO(DATE_1, START_TIME_1, START_TIME_1.minusHours(1), requesterAccountId))
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun testUpdatePresentMeeting() {
        val accountId = createAccount1()
        val requesterAccountId = createAccount2()
        createAvailability(accountId)
        createAvailability2(accountId)
        createAvailability(requesterAccountId)
        createAvailability2(requesterAccountId)
        val meetingLinkId = createMeetingLink(accountId)
        val meetingId = createMeeting(accountId, requesterAccountId, meetingLinkId)
        webTestClient
            .put()
            .uri("/accounts/$accountId/meeting-links/$meetingLinkId/meetings/$meetingId")
            .bodyValue(newMeetingDTO(requesterAccountId))
            .exchange()
            .expectStatus().isNoContent
    }

    @Test
    fun testDeleteAbsentMeeting() {
        webTestClient
            .delete()
            .uri("/accounts/1/meeting-links/1/meetings/1")
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun testDeleteMeeting() {
        val accountId = createAccount1()
        val requesterAccountId = createAccount2()
        createAvailability(accountId)
        createAvailability2(accountId)
        createAvailability(requesterAccountId)
        createAvailability2(requesterAccountId)
        val meetingLinkId = createMeetingLink(accountId)
        val meetingId = createMeeting(accountId, requesterAccountId, meetingLinkId)
        webTestClient
            .delete()
            .uri("/accounts/$accountId/meeting-links/$meetingLinkId/meetings/$meetingId")
            .exchange()
            .expectStatus().isNoContent
    }
}
