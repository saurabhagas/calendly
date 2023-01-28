package com.harbor.calendly.controller

import com.harbor.calendly.dto.AccountDTO
import com.harbor.calendly.service.AccountService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping(Endpoints.ACCOUNT_URL)
class AccountController(val accountService: AccountService) {
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.CREATED)
    fun createAccount(
        @Valid @RequestBody accountDTO: AccountDTO
    ): Int = accountService.createAccount(accountDTO)

    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    fun getAccounts(
        @RequestParam("name") name: String?,
        @RequestParam("email") email: String?,
        @RequestParam("phone") phone: String?,
    ): List<AccountDTO> {
        return accountService.getAccounts(name, email, phone)
    }

    @GetMapping("/{accountId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    fun getAccount(
        @PathVariable("accountId") accountId: String
    ): AccountDTO = accountService.getAccount(accountId.toInt())

    @PutMapping("/{accountId}", consumes = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateAccount(
        @Valid @RequestBody accountDTO: AccountDTO,
        @PathVariable("accountId") accountId : Int
    ): Unit = accountService.updateAccount(accountId, accountDTO)

    @DeleteMapping("/{accountId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteAccount(
        @PathVariable("accountId") accountId: String
    ): Unit = accountService.deactivateAccount(accountId.toInt())
}
