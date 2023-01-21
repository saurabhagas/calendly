package com.harbor.calendly

import com.harbor.calendly.dto.MeetingLinkDTO
import org.junit.jupiter.api.Test

/**
 * Integration tests for /meeting-links resource.
 */
class MeetingLinkControllerTest : BaseTest() {
    @Test
    fun testCreateMeetingLinkWithInvalidDTO() {
        webTestClient
            .post()
            .uri("/meeting-links")
            .bodyValue(SomeClass(123, 1234567890))
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun testCreateMeetingLinkDatesIsEmpty() {
        webTestClient
            .post()
            .uri("/meeting-links")
            .bodyValue(MeetingLinkDTO(1, DURATION_IN_MINS, emptyList()))
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun testCreateMeetingLinkOnDeactivatedAccount() {
        val accountId = createAccount()
        deactivateAccount(accountId)
        webTestClient
            .post()
            .uri("/meeting-links")
            .bodyValue(MeetingLinkDTO(accountId, DURATION_IN_MINS, DATES))
            .exchange()
            .expectStatus().isForbidden
    }

    @Test
    fun testCreateMeetingLinkForDisallowedDuration() {
        webTestClient
            .post()
            .uri("/meeting-links")
            .bodyValue(MeetingLinkDTO(1, 1, DATES))
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun testCreateMeetingLinkWithPartialAvailability() {
        val accountId = createAccount()
        createAvailability(accountId)
        webTestClient
            .post()
            .uri("/meeting-links")
            .bodyValue(MeetingLinkDTO(accountId, DURATION_IN_MINS, DATES))
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun testCreateMeetingLink() {
        val accountId = createAccount()
        createAvailability(accountId)
        createAvailability2(accountId)
        webTestClient
            .post()
            .uri("/meeting-links")
            .bodyValue(MeetingLinkDTO(accountId, DURATION_IN_MINS, DATES))
            .exchange()
            .expectStatus().isCreated
            .expectBody(Int::class.java)
    }

    @Test
    fun testGetAbsentMeetingLink() {
        webTestClient
            .get()
            .uri("/meeting-links/1")
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun testGetPresentMeetingLink() {
        val accountId = createAccount()
        createAvailability(accountId)
        createAvailability2(accountId)
        val meetingLinkId = createMeetingLink(accountId)
        webTestClient
            .get()
            .uri("/meeting-links/{meetingLinkId}", meetingLinkId)
            .exchange()
            .expectStatus().isOk
            .expectBody(MeetingLinkDTO::class.java)
            .isEqualTo(MeetingLinkDTO(accountId, DURATION_IN_MINS, DATES))
    }

    @Test
    fun testUpdateAbsentMeetingLink() {
        webTestClient
            .put()
            .uri("/meeting-links/1")
            .bodyValue(MeetingLinkDTO(1, DURATION_IN_MINS, DATES))
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun testUpdateMeetingLinkWhenDatesListIsEmpty() {
        webTestClient
            .put()
            .uri("/meeting-links/1")
            .bodyValue(MeetingLinkDTO(1, DURATION_IN_MINS, emptyList()))
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun testUpdatePresentMeetingLink() {
        val accountId = createAccount()
        createAvailability(accountId)
        createAvailability2(accountId)
        val meetingLinkId = createMeetingLink(accountId)
        webTestClient
            .put()
            .uri("/meeting-links/{meetingLinkId}", meetingLinkId)
            .bodyValue(MeetingLinkDTO(accountId, DURATION_IN_MINS, DATES))
            .exchange()
            .expectStatus().isNoContent
    }

    @Test
    fun testDeleteAbsentMeetingLink() {
        webTestClient
            .delete()
            .uri("/meeting-links/1")
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun testDeleteMeetingLink() {
        val accountId = createAccount()
        createAvailability(accountId)
        createAvailability2(accountId)
        val meetingLinkId = createMeetingLink(accountId)
        webTestClient
            .delete()
            .uri("/meeting-links/{meetingLinkId}", meetingLinkId)
            .exchange()
            .expectStatus().isNoContent
    }
}
