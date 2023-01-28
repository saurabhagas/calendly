package com.harbor.calendly

import com.harbor.calendly.dto.AccountDTO
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Integration tests for /accounts resource.
 */
class AccountControllerTest : BaseTest() {
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
    fun testCreateAccountWithInvalidEmail() {
        webTestClient
            .post()
            .uri("/accounts")
            .bodyValue(AccountDTO(NAME_1, "blah", PHONE_1))
            .exchange()
            .expectStatus().isBadRequest
            .expectBody(String::class.java).isEqualTo("\"email must be valid\"")
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
        createAccount1()
        webTestClient
            .post()
            .uri("/accounts")
            .bodyValue(AccountDTO(NAME_1, EMAIL_1, PHONE_2))
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun testCreateAccountWithExistingPhone() {
        createAccount1()
        webTestClient
            .post()
            .uri("/accounts")
            .bodyValue(AccountDTO(NAME_1, EMAIL_2, PHONE_1))
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun testGetAllAccountsWhenNoAccountsExist() {
        webTestClient
            .get()
            .uri("/accounts")
            .exchange()
            .expectStatus().isOk
            .expectBodyList(AccountDTO::class.java)
            .hasSize(0)
    }

    @Test
    fun testGetAllAccountsWithNoMatchingNames() {
        createAccount1()
        webTestClient
            .get()
            .uri("/accounts?name=blah")
            .exchange()
            .expectStatus().isOk
            .expectBodyList(AccountDTO::class.java)
            .hasSize(0)
    }

    @Test
    fun testGetAllAccountsWithMatchingNames() {
        createAccount1()
        webTestClient
            .get()
            .uri("/accounts?name=$NAME_1")
            .exchange()
            .expectStatus().isOk
            .expectBodyList(AccountDTO::class.java)
            .hasSize(1)
            .contains(AccountDTO(NAME_1, EMAIL_1, PHONE_1))
    }

    @Test
    fun testGetAllAccountsWithMatchingNamesButNonMatchingEmail() {
        createAccount1()
        webTestClient
            .get()
            .uri("/accounts?name=$NAME_1&email=blah@blah.com")
            .exchange()
            .expectStatus().isOk
            .expectBodyList(AccountDTO::class.java)
            .hasSize(0)
    }

    @Test
    fun testGetAllAccountsWithMatchingNamesAndMatchingEmail() {
        createAccount1()
        webTestClient
            .get()
            .uri("/accounts?name=$NAME_1&email=$EMAIL_1")
            .exchange()
            .expectStatus().isOk
            .expectBodyList(AccountDTO::class.java)
            .hasSize(1)
            .contains(AccountDTO(NAME_1, EMAIL_1, PHONE_1))
    }

    @Test
    fun testGetAllAccountsWithNameMatchesMultipleAccounts() {
        createAccount1()
        createAccount2()
        webTestClient
            .get()
            .uri("/accounts?name=$NAME_1")
            .exchange()
            .expectStatus().isOk
            .expectBodyList(AccountDTO::class.java)
            .hasSize(2)
            .contains(AccountDTO(NAME_1, EMAIL_1, PHONE_1))
            .contains(AccountDTO(NAME_1, EMAIL_2, PHONE_2))
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
        val accountId = createAccount1()
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
        val accountId = createAccount1()
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
        val accountId = createAccount1()
        webTestClient
            .delete()
            .uri("/accounts/{accountId}", accountId)
            .exchange()
            .expectStatus().isNoContent
    }

    @Test
    fun testGetDeactivatedAccount() {
        val accountId = createAccount1()
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
}
