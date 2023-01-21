package com.harbor.calendly

import com.harbor.calendly.dto.MeetingDTO
import org.junit.jupiter.api.Test

/**
 * Integration tests for /meetings resource.
 */
class MeetingControllerTest:BaseTest() {
    @Test
    fun testCreateMeetingWithInvalidDTO() {
        webTestClient
            .post()
            .uri("/meetings")
            .bodyValue(SomeClass(123, 1234567890))
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun testCreateMeetingWhenEndTimeEarlierThanStartTime() {
        webTestClient
            .post()
            .uri("/meetings")
            .bodyValue(MeetingDTO(1, DATE_1,
                START_TIME_1, START_TIME_1.minusHours(1), REQUESTER_NAME, REQUESTER_EMAIL, REQUESTER_PHONE))
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
            .uri("/meetings")
            .bodyValue(newMeetingDTO(meetingLinkId))
            .exchange()
            .expectStatus().isCreated
            .expectBody(Int::class.java)
    }

    @Test
    fun testGetAbsentMeeting() {
        webTestClient
            .get()
            .uri("/meetings/1")
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun testGetPresentMeeting() {
        val accountId = createAccount()
        createAvailability(accountId)
        createAvailability2(accountId)
        val meetingLinkId = createMeetingLink(accountId)
        val meetingId = createMeeting(meetingLinkId)
        webTestClient
            .get()
            .uri("/meetings/{meetingId}", meetingId)
            .exchange()
            .expectStatus().isOk
            .expectBody(MeetingDTO::class.java)
            .isEqualTo(newMeetingDTO(meetingLinkId))
    }

    @Test
    fun testUpdateAbsentMeeting() {
        webTestClient
            .put()
            .uri("/meetings/1")
            .bodyValue(newMeetingDTO(1))
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun testUpdateMeetingWhenEndTimeEarlierThanStartTime() {
        val accountId = createAccount()
        createAvailability(accountId)
        createAvailability2(accountId)
        val meetingLinkId = createMeetingLink(accountId)
        val meetingId = createMeeting(meetingLinkId)
        webTestClient
            .put()
            .uri("/meetings/{meetingId}", meetingId)
            .bodyValue(MeetingDTO(meetingLinkId, DATE_1, START_TIME_1, START_TIME_1.minusHours(1), REQUESTER_NAME, REQUESTER_EMAIL, REQUESTER_PHONE))
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun testUpdatePresentMeeting() {
        val accountId = createAccount()
        createAvailability(accountId)
        createAvailability2(accountId)
        val meetingLinkId = createMeetingLink(accountId)
        val meetingId = createMeeting(meetingLinkId)
        webTestClient
            .put()
            .uri("/meetings/{meetingId}", meetingId)
            .bodyValue(newMeetingDTO(meetingLinkId))
            .exchange()
            .expectStatus().isNoContent
    }

    @Test
    fun testDeleteAbsentMeeting() {
        webTestClient
            .delete()
            .uri("/meetings/1")
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun testDeleteMeeting() {
        val accountId = createAccount()
        createAvailability(accountId)
        createAvailability2(accountId)
        val meetingLinkId = createMeetingLink(accountId)
        val meetingId = createMeeting(meetingLinkId)
        webTestClient
            .delete()
            .uri("/meetings/{meetingId}", meetingId)
            .exchange()
            .expectStatus().isNoContent
    }
}
