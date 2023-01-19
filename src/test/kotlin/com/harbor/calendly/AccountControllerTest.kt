package com.harbor.calendly

import com.harbor.calendly.dto.AccountDTO
import com.harbor.calendly.repository.AccountRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient


/**
 * Functional tests for /accounts resource.
 * Serves the following purposes:
 * 1. Ensure that functionality is correct during development, and dev changes don't break functionality
 * 2. Functionality acceptance
 * 3. Documentation for the APIs
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
internal class AccountControllerTest {

    companion object {
        private const val NAME_1 = "Saurabh"
        private const val NAME_2 = "Suresh"
        private const val EMAIL_1 = "saurabh@somedomain.com"
        private const val EMAIL_2 = "saurabh@habor.com"
        private const val PHONE_1 = "123-234-345"
        private const val PHONE_2 = "888-888-888"
    }

    @Autowired
    lateinit var webTestClient: WebTestClient

    @Autowired
    lateinit var accountRepository: AccountRepository

    @BeforeEach
    fun setUp() {
        accountRepository.deleteAll()
    }

    @Test
    fun testCreateAccountWithInvalidDTO() {
        webTestClient
            .post()
            .uri("/accounts")
            .bodyValue(SomeClass(123, 1234567890))
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun testCreateAccountWithAbsentName() {
        webTestClient
            .post()
            .uri("/accounts")
            .bodyValue(AccountDTO("", EMAIL_1, PHONE_1))
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun testCreateAccountWithAbsentEmail() {
        webTestClient
            .post()
            .uri("/accounts")
            .bodyValue(AccountDTO(NAME_1, "", PHONE_1))
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun testCreateAccountWithAbsentPhone() {
        webTestClient
            .post()
            .uri("/accounts")
            .bodyValue(AccountDTO(NAME_1, EMAIL_1, ""))
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun testCreateAccount() {
        webTestClient
            .post()
            .uri("/accounts")
            .bodyValue(AccountDTO(NAME_1, EMAIL_1, PHONE_1))
            .exchange()
            .expectStatus().isCreated
            .expectBody(Int::class.java)
    }

    @Test
    fun testCreateAccountWithExistingEmail() {
        createAccount()
        webTestClient
            .post()
            .uri("/accounts")
            .bodyValue(AccountDTO(NAME_2, EMAIL_1, PHONE_2))
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun testCreateAccountWithExistingPhone() {
        createAccount()
        webTestClient
            .post()
            .uri("/accounts")
            .bodyValue(AccountDTO(NAME_2, EMAIL_2, PHONE_1))
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun testGetAbsentAccount() {
        webTestClient
            .get()
            .uri("/accounts/1")
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun testGetPresentAccount() {
        val accountId = createAccount()

        webTestClient
            .get()
            .uri("/accounts/{accountId}", accountId)
            .exchange()
            .expectStatus().isOk
            .expectBody(AccountDTO::class.java)
            .isEqualTo(AccountDTO(NAME_1, EMAIL_1, PHONE_1))
    }

    @Test
    fun testUpdateAbsentAccount() {
        webTestClient
            .put()
            .uri("/accounts/1")
            .bodyValue(AccountDTO(NAME_1, EMAIL_1, PHONE_1))
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun testUpdatePresentAccount() {
        val accountId = createAccount()

        webTestClient
            .put()
            .uri("/accounts/{accountId}", accountId)
            .bodyValue(AccountDTO(NAME_1, EMAIL_2, PHONE_1))
            .exchange()
            .expectStatus().isNoContent
    }

    @Test
    fun testDeactivateAbsentAccount() {
        webTestClient
            .delete()
            .uri("/accounts/1")
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun testDeactivatePresentAccount() {
        val accountId = createAccount()
        webTestClient
            .delete()
            .uri("/accounts/{accountId}", accountId)
            .exchange()
            .expectStatus().isNoContent
    }

    @Test
    fun testGetDeactivatedAccount() {
        val accountId = createAccount()
        println("#####Account created with id: $accountId")
        webTestClient
            .delete()
            .uri("/accounts/{accountId}", accountId)
            .exchange()
        webTestClient
            .get()
            .uri("/accounts/{accountId}", accountId)
            .exchange()
            .expectStatus().isForbidden
    }

    private fun createAccount() = webTestClient
        .post()
        .uri("/accounts")
        .bodyValue(AccountDTO(NAME_1, EMAIL_1, PHONE_1))
        .exchange()
        .expectBody(Int::class.java)
        .returnResult()
        .responseBody
}