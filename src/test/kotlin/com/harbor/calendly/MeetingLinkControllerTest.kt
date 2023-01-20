package com.harbor.calendly

import com.harbor.calendly.dto.AccountDTO
import com.harbor.calendly.dto.MeetingLinkDTO
import com.harbor.calendly.repository.AccountRepository
import com.harbor.calendly.repository.MeetingLinkRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.LocalDate


/**
 * Integration tests for /meeting-links resource.
 * Serves the following purposes:
 * 1. Helps with CI of new changes
 * 2. Documentation for the APIs on /meeting-links resource
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
internal class MeetingLinkControllerTest {
    companion object {
        private val START_DATE = LocalDate.of(2023, 1, 30)
        private val END_DATE = LocalDate.of(2023, 2, 15)
    }

    @Autowired
    lateinit var webTestClient: WebTestClient

    @Autowired
    lateinit var accountRepository: AccountRepository

    @Autowired
    lateinit var meetingLinkRepository: MeetingLinkRepository

    @BeforeEach
    fun setUp() {
        meetingLinkRepository.deleteAll()
        accountRepository.deleteAll()
    }

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
    fun testCreateMeetingLinkWhenEndTimeEarlierThanStartTime() {
        webTestClient
            .post()
            .uri("/meeting-links")
            .bodyValue(MeetingLinkDTO(1, START_DATE, START_DATE.minusDays(1)))
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun testCreateMeetingLink() {
        val accountId = createAccount()
        webTestClient
            .post()
            .uri("/meeting-links")
            .bodyValue(MeetingLinkDTO(accountId, START_DATE, END_DATE))
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
        val (accountId, meetingLinkId) = createMeetingLink()
        webTestClient
            .get()
            .uri("/meeting-links/{meetingLinkId}", meetingLinkId)
            .exchange()
            .expectStatus().isOk
            .expectBody(MeetingLinkDTO::class.java)
            .isEqualTo(MeetingLinkDTO(accountId, START_DATE, END_DATE))
    }

    @Test
    fun testUpdateAbsentMeetingLink() {
        webTestClient
            .put()
            .uri("/meeting-links/1")
            .bodyValue(MeetingLinkDTO(1, START_DATE, END_DATE))
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun testUpdateMeetingLinkWhenEndTimeEarlierThanStartTime() {
        val (accountId, meetingLinkId) = createMeetingLink()
        webTestClient
            .put()
            .uri("/meeting-links/{meetingLinkId}", meetingLinkId)
            .bodyValue(MeetingLinkDTO(accountId, START_DATE, START_DATE.minusDays(1)))
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun testUpdatePresentMeetingLink() {
        val (accountId, meetingLinkId) = createMeetingLink()
        webTestClient
            .put()
            .uri("/meeting-links/{meetingLinkId}", meetingLinkId)
            .bodyValue(MeetingLinkDTO(accountId, START_DATE, START_DATE.plusDays(1)))
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
        val (_, meetingLinkId) = createMeetingLink()
        webTestClient
            .delete()
            .uri("/meeting-links/{meetingLinkId}", meetingLinkId)
            .exchange()
            .expectStatus().isNoContent
    }

    private fun createMeetingLink(): Pair<Int, Int> {
        val accountId = createAccount()
        val meetingLinkId = webTestClient
            .post()
            .uri("/meeting-links")
            .bodyValue(MeetingLinkDTO(accountId, START_DATE, END_DATE))
            .exchange()
            .expectBody(Int::class.java)
            .returnResult()
            .responseBody!!
        return accountId to meetingLinkId
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
