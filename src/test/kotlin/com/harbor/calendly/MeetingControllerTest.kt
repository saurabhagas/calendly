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
            .bodyValue(MeetingDTO(DATE_1, START_TIME_1, START_TIME_1.minusHours(1), REQUESTER_NAME, REQUESTER_EMAIL, REQUESTER_PHONE))
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun testCreateMeeting() {
        val accountId = createAccount()
        createAvailability(accountId)
        createAvailability2(accountId)
        val meetingLinkId = createMeetingLink(accountId)
        webTestClient
            .post()
            .uri("/accounts/$accountId/meeting-links/$meetingLinkId/meetings")
            .bodyValue(newMeetingDTO())
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
        val accountId = createAccount()
        createAvailability(accountId)
        createAvailability2(accountId)
        val meetingLinkId = createMeetingLink(accountId)
        val meetingId = createMeeting(accountId, meetingLinkId)
        webTestClient
            .get()
            .uri("/accounts/$accountId/meeting-links/$meetingLinkId/meetings/$meetingId")
            .exchange()
            .expectStatus().isOk
            .expectBody(MeetingDTO::class.java)
            .isEqualTo(newMeetingDTO())
    }

    @Test
    fun testUpdateAbsentMeeting() {
        webTestClient
            .put()
            .uri("/accounts/1/meeting-links/1/meetings/1")
            .bodyValue(newMeetingDTO())
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun testUpdateMeetingWhenEndTimeEarlierThanStartTime() {
        val accountId = createAccount()
        createAvailability(accountId)
        createAvailability2(accountId)
        val meetingLinkId = createMeetingLink(accountId)
        val meetingId = createMeeting(accountId, meetingLinkId)
        webTestClient
            .put()
            .uri("/accounts/$accountId/meeting-links/$meetingLinkId/meetings/$meetingId")
            .bodyValue(MeetingDTO(DATE_1, START_TIME_1, START_TIME_1.minusHours(1), REQUESTER_NAME, REQUESTER_EMAIL, REQUESTER_PHONE))
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun testUpdatePresentMeeting() {
        val accountId = createAccount()
        createAvailability(accountId)
        createAvailability2(accountId)
        val meetingLinkId = createMeetingLink(accountId)
        val meetingId = createMeeting(accountId, meetingLinkId)
        webTestClient
            .put()
            .uri("/accounts/$accountId/meeting-links/$meetingLinkId/meetings/$meetingId")
            .bodyValue(newMeetingDTO())
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
        val accountId = createAccount()
        createAvailability(accountId)
        createAvailability2(accountId)
        val meetingLinkId = createMeetingLink(accountId)
        val meetingId = createMeeting(accountId, meetingLinkId)
        webTestClient
            .delete()
            .uri("/accounts/$accountId/meeting-links/$meetingLinkId/meetings/$meetingId")
            .exchange()
            .expectStatus().isNoContent
    }
}
