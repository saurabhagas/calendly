package com.harbor.calendly.controller

import com.harbor.calendly.dto.AccountDTO
import com.harbor.calendly.service.AccountService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/accounts")
class AccountController(val accountService: AccountService) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createAccount(
        @Valid @RequestBody accountDTO: AccountDTO
    ) = accountService.createAccount(accountDTO)

    @GetMapping("/{accountId}")
    @ResponseStatus(HttpStatus.OK)
    fun getAccount(
        @PathVariable("accountId") accountId: String
    ) = accountService.getAccount(accountId.toInt())

    @PutMapping("/{accountId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateAccount(
        @Valid @RequestBody accountDTO: AccountDTO,
        @PathVariable("accountId") accountId : Int
    ) = accountService.updateAccount(accountId, accountDTO)

    @DeleteMapping("/{accountId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteAccount(
        @PathVariable("accountId") accountId: String
    ) = accountService.deactivateAccount(accountId.toInt())
}
