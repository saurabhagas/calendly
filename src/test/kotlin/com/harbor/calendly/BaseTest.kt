package com.harbor.calendly

import com.harbor.calendly.dto.AccountDTO
import com.harbor.calendly.dto.AvailabilityDTO
import com.harbor.calendly.dto.MeetingDTO
import com.harbor.calendly.dto.MeetingLinkDTO
import com.harbor.calendly.repository.AccountRepository
import com.harbor.calendly.repository.AvailabilityRepository
import com.harbor.calendly.repository.MeetingLinkRepository
import com.harbor.calendly.repository.MeetingRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class BaseTest {
    companion object {
        const val NAME_1 = "Saurabh"
        const val EMAIL_1 = "saurabh@somedomain.com"
        const val EMAIL_2 = "saurabh@habor.com"
        const val PHONE_1 = "123-234-345"
        const val PHONE_2 = "888-888-888"
        const val DURATION_IN_MINS = 30
        val DATE_1 = LocalDate.of(2023, 1, 30)
        val DATE_2 = LocalDate.of(2023, 1, 31)
        val DATES = listOf(DATE_1, DATE_2)
        val START_TIME_1 = LocalTime.of(9, 30)
        val END_TIME_1 = LocalTime.of(17, 30)
        val START_TIME_2 = LocalTime.of(12, 0)
        val END_TIME_2 = LocalTime.of(12, 30)
        val DAY_1 = DayOfWeek.MONDAY
        val DAY_2 = DayOfWeek.TUESDAY
    }

    @Autowired
    lateinit var webTestClient: WebTestClient

    @Autowired
    lateinit var accountRepository: AccountRepository

    @Autowired
    lateinit var availabilityRepository: AvailabilityRepository

    @Autowired
    lateinit var meetingLinkRepository: MeetingLinkRepository

    @Autowired
    lateinit var meetingRepository: MeetingRepository

    fun createAccount1() = webTestClient
        .post()
        .uri("/accounts")
        .bodyValue(AccountDTO(NAME_1, EMAIL_1, PHONE_1))
        .exchange()
        .expectBody(Int::class.java)
        .returnResult()
        .responseBody!!

    fun createAccount2() = webTestClient
        .post()
        .uri("/accounts")
        .bodyValue(AccountDTO(NAME_1, EMAIL_2, PHONE_2))
        .exchange()
        .expectBody(Int::class.java)
        .returnResult()
        .responseBody!!

    fun deactivateAccount(
        accountId: Int,
    ) = webTestClient
        .delete()
        .uri("/accounts/{accountId}", accountId)
        .exchange()
        .expectStatus().isNoContent

    fun createAvailability(
        accountId: Int,
        dayOfWeek: DayOfWeek = DAY_1,
        startTime: LocalTime = START_TIME_1,
        endTime: LocalTime = END_TIME_1
    ) = webTestClient
        .post()
        .uri("/accounts/$accountId/availability")
        .bodyValue(AvailabilityDTO(dayOfWeek, startTime, endTime))
        .exchange()
        .expectBody(Int::class.java)
        .returnResult()
        .responseBody!!

    fun createAvailability2(
        accountId: Int,
        dayOfWeek: DayOfWeek = DAY_2,
        startTime: LocalTime = START_TIME_2,
        endTime: LocalTime = END_TIME_2
    ) = webTestClient
        .post()
        .uri("/accounts/$accountId/availability")
        .bodyValue(AvailabilityDTO(dayOfWeek, startTime, endTime))
        .exchange()
        .expectBody(Int::class.java)
        .returnResult()
        .responseBody!!

    fun getAllAvailabilities(
        accountId: Int
    ) = webTestClient
        .get()
        .uri("/accounts/$accountId/availability")
        .exchange()
        .expectBodyList(AvailabilityDTO::class.java)
        .returnResult()
        .responseBody!!

    fun getOverlappingAvailabilities(
        hostAccountId: Int,
        requesterAccountId: Int
    ) = webTestClient
        .get()
        .uri("/accounts/$hostAccountId/overlap/$requesterAccountId")
        .exchange()
        .expectBodyList(AvailabilityDTO::class.java)
        .returnResult()
        .responseBody!!

    fun createMeetingLink(
        accountId: Int
    ) = webTestClient
        .post()
        .uri("/accounts/$accountId/meeting-links")
        .bodyValue(MeetingLinkDTO(DURATION_IN_MINS, DATES))
        .exchange()
        .expectBody(Int::class.java)
        .returnResult()
        .responseBody!!

    fun createMeeting(
        hostAccountId: Int,
        requesterAccountId: Int,
        meetingLinkId: Int
    ) = webTestClient
        .post()
        .uri("/accounts/$hostAccountId/meeting-links/$meetingLinkId/meetings")
        .bodyValue(newMeetingDTO(requesterAccountId))
        .exchange()
        .expectBody(Int::class.java)
        .returnResult()
        .responseBody!!

    fun createMeeting2(
        hostAccountId: Int,
        requesterAccountId: Int,
        meetingLinkId: Int
    ) = webTestClient
        .post()
        .uri("/accounts/$hostAccountId/meeting-links/$meetingLinkId/meetings")
        .bodyValue(MeetingDTO(DATE_2, START_TIME_2, END_TIME_2, requesterAccountId))
        .exchange()
        .expectBody(Int::class.java)
        .returnResult()
        .responseBody!!

    fun newMeetingDTO(requesterAccountId: Int) = MeetingDTO(DATE_1, START_TIME_2, END_TIME_2, requesterAccountId)

    data class SomeClass(
        val id: Int,
        val createdAt: Long,
    )
}
