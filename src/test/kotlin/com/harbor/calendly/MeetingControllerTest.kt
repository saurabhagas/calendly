package com.harbor.calendly

import com.harbor.calendly.dto.AccountDTO
import com.harbor.calendly.dto.MeetingDTO
import com.harbor.calendly.dto.MeetingLinkDTO
import com.harbor.calendly.repository.AccountRepository
import com.harbor.calendly.repository.MeetingLinkRepository
import com.harbor.calendly.repository.MeetingRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.LocalDate
import java.time.LocalTime


/**
 * Integration tests for /meetings resource.
 * Serves the following purposes:
 * 1. Helps with CI of new changes
 * 2. Documentation for the APIs on /meetings resource
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
internal class MeetingControllerTest {
    companion object {
        private const val DURATION_IN_MINS = 30
        private val START_DATE = LocalDate.of(2023, 1, 30)
        private val END_DATE = LocalDate.of(2023, 2, 15)
        private val START_TIME = LocalTime.of(9, 30)
        private val END_TIME = LocalTime.of(17, 30)
        private const val REQUESTER_NAME = "Requester"
        private const val REQUESTER_EMAIL = "requester@somedomain.com"
        private const val REQUESTER_PHONE = "900-000-000"
    }

    @Autowired
    lateinit var webTestClient: WebTestClient

    @Autowired
    lateinit var accountRepository: AccountRepository

    @Autowired
    lateinit var meetingLinkRepository: MeetingLinkRepository

    @Autowired
    lateinit var meetingRepository: MeetingRepository

    @BeforeEach
    fun setUp() {
        meetingRepository.deleteAll()
        meetingLinkRepository.deleteAll()
        accountRepository.deleteAll()
    }

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
            .bodyValue(MeetingDTO(1, START_DATE, START_TIME, START_TIME.minusHours(1), REQUESTER_NAME, REQUESTER_EMAIL, REQUESTER_PHONE))
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun testCreateMeeting() {
        val meetingLinkId = createMeetingLink()
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
        val (meetingId, meetingLinkId) = createMeeting()
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
        val (meetingId, meetingLinkId) = createMeeting()
        webTestClient
            .put()
            .uri("/meetings/{meetingId}", meetingId)
            .bodyValue(MeetingDTO(meetingLinkId, START_DATE, START_TIME, START_TIME.minusHours(1), REQUESTER_NAME, REQUESTER_EMAIL, REQUESTER_PHONE))
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun testUpdatePresentMeeting() {
        val (meetingId, meetingLinkId) = createMeeting()
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
        val (meetingId, _) = createMeeting()
        webTestClient
            .delete()
            .uri("/meetings/{meetingId}", meetingId)
            .exchange()
            .expectStatus().isNoContent
    }

    private fun createMeeting(): Pair<Int, Int> {
        val meetingLinkId = createMeetingLink()
        val meetingId = webTestClient
            .post()
            .uri("/meetings")
            .bodyValue(newMeetingDTO(meetingLinkId))
            .exchange()
            .expectBody(Int::class.java)
            .returnResult()
            .responseBody!!
        return meetingId to meetingLinkId
    }

    private fun newMeetingDTO(meetingLinkId: Int) =
        MeetingDTO(meetingLinkId, START_DATE, START_TIME, END_TIME, REQUESTER_NAME, REQUESTER_EMAIL, REQUESTER_PHONE)

    private fun createMeetingLink(): Int {
        val accountId = createAccount()
        return webTestClient
            .post()
            .uri("/meeting-links")
            .bodyValue(MeetingLinkDTO(accountId, DURATION_IN_MINS, START_DATE, END_DATE))
            .exchange()
            .expectBody(Int::class.java)
            .returnResult()
            .responseBody!!
    }

    private fun createAccount() = webTestClient
        .post()
        .uri("/accounts")
        .bodyValue(AccountDTO("Saurabh Agarwal", "saurabh.agarwal@somedomain.com", "123-234-345"))
        .exchange()
        .expectBody(Int::class.java)
        .returnResult()
        .responseBody!!
}
