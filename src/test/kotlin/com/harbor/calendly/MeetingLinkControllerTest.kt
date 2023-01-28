package com.harbor.calendly

import com.harbor.calendly.dto.MeetingLinkDTO
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Integration tests for /meeting-links resource.
 */
class MeetingLinkControllerTest : BaseTest() {
    @BeforeEach
    fun setUp() {
        meetingLinkRepository.deleteAll()
        availabilityRepository.deleteAll()
        accountRepository.deleteAll()
    }

    @Test
    fun testCreateMeetingLinkWithInvalidDTO() {
        webTestClient
            .post()
            .uri("/accounts/1/meeting-links")
            .bodyValue(SomeClass(123, 1234567890))
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun testCreateMeetingLinkDatesIsEmpty() {
        webTestClient
            .post()
            .uri("/accounts/1/meeting-links")
            .bodyValue(MeetingLinkDTO(DURATION_IN_MINS, emptyList()))
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun testCreateMeetingLinkOnDeactivatedAccount() {
        val accountId = createAccount1()
        deactivateAccount(accountId)
        webTestClient
            .post()
            .uri("/accounts/$accountId/meeting-links")
            .bodyValue(MeetingLinkDTO(DURATION_IN_MINS, DATES))
            .exchange()
            .expectStatus().isForbidden
    }

    @Test
    fun testCreateMeetingLinkForDisallowedDuration() {
        webTestClient
            .post()
            .uri("/accounts/1/meeting-links")
            .bodyValue(MeetingLinkDTO(1, DATES))
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun testCreateMeetingLinkWithPartialAvailability() {
        val accountId = createAccount1()
        createAvailability(accountId)
        webTestClient
            .post()
            .uri("/accounts/$accountId/meeting-links")
            .bodyValue(MeetingLinkDTO(DURATION_IN_MINS, DATES))
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun testCreateMeetingLink() {
        val accountId = createAccount1()
        createAvailability(accountId)
        createAvailability2(accountId)
        webTestClient
            .post()
            .uri("/accounts/$accountId/meeting-links")
            .bodyValue(MeetingLinkDTO(DURATION_IN_MINS, DATES))
            .exchange()
            .expectStatus().isCreated
            .expectBody(Int::class.java)
    }

    @Test
    fun testGetAbsentMeetingLink() {
        webTestClient
            .get()
            .uri("/accounts/1/meeting-links/1")
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun testGetPresentMeetingLink() {
        val accountId = createAccount1()
        createAvailability(accountId)
        createAvailability2(accountId)
        val meetingLinkId = createMeetingLink(accountId)
        webTestClient
            .get()
            .uri("/accounts/$accountId/meeting-links/{meetingLinkId}", meetingLinkId)
            .exchange()
            .expectStatus().isOk
            .expectBody(MeetingLinkDTO::class.java)
            .isEqualTo(MeetingLinkDTO(DURATION_IN_MINS, DATES))
    }

    @Test
    fun testUpdateAbsentMeetingLink() {
        webTestClient
            .put()
            .uri("/accounts/1/meeting-links/1")
            .bodyValue(MeetingLinkDTO(DURATION_IN_MINS, DATES))
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun testUpdateMeetingLinkWhenDatesListIsEmpty() {
        webTestClient
            .put()
            .uri("/accounts/1/meeting-links/1")
            .bodyValue(MeetingLinkDTO(DURATION_IN_MINS, emptyList()))
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun testUpdatePresentMeetingLink() {
        val accountId = createAccount1()
        createAvailability(accountId)
        createAvailability2(accountId)
        val meetingLinkId = createMeetingLink(accountId)
        webTestClient
            .put()
            .uri("/accounts/$accountId/meeting-links/{meetingLinkId}", meetingLinkId)
            .bodyValue(MeetingLinkDTO(DURATION_IN_MINS, DATES))
            .exchange()
            .expectStatus().isNoContent
    }

    @Test
    fun testDeleteAbsentMeetingLink() {
        webTestClient
            .delete()
            .uri("/accounts/1/meeting-links/1")
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun testDeleteMeetingLink() {
        val accountId = createAccount1()
        createAvailability(accountId)
        createAvailability2(accountId)
        val meetingLinkId = createMeetingLink(accountId)
        webTestClient
            .delete()
            .uri("/accounts/$accountId/meeting-links/$meetingLinkId")
            .exchange()
            .expectStatus().isNoContent
    }
}
