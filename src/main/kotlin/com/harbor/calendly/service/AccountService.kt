package com.harbor.calendly.service

import com.harbor.calendly.dto.AccountDTO
import com.harbor.calendly.entity.Account
import com.harbor.calendly.exception.InactiveAccountException
import com.harbor.calendly.exception.NotFoundException
import com.harbor.calendly.repository.AccountRepository
import com.harbor.calendly.utils.toAccount
import com.harbor.calendly.utils.toAccountDTO
import mu.KLogging
import org.springframework.stereotype.Service

@Service
class AccountService(val accountRepository: AccountRepository) {
    companion object : KLogging()

    fun createAccount(accountDTO: AccountDTO): Int {
        logger.info("createAccount called with: {}", accountDTO)
        require(accountRepository.countByEmail(accountDTO.email) == 0) { "Email ${accountDTO.email} already exists"}
        require(accountRepository.countByPhone(accountDTO.phone) == 0) { "Phone ${accountDTO.phone} already exists"}
        val result = accountRepository.save(accountDTO.toAccount())
        logger.info("create result: {}", result)
        return requireNotNull(result.id)
    }

    fun getAccount(accountId: Int): AccountDTO {
        logger.info("getAccount called with: {}", accountId)
        val account = validateAndGetAccount(accountId)
        logger.info("fetched account: {}", account)
        return account.toAccountDTO()
    }

    fun updateAccount(accountId: Int, accountDTO: AccountDTO) {
        logger.info("updateAccount called with: {} {}", accountId, accountDTO)
        val account = validateAndGetAccount(accountId)
        logger.info("fetched account: {}", account)
        accountRepository.save(accountDTO.toAccount(accountId))
    }

    fun deactivateAccount(accountId: Int) {
        logger.info("deleteAccount called with: {}", accountId)
        val account = validateAndGetAccount(accountId)
        logger.info("fetched account: {}", account)
        val updateResult = accountRepository.deactivateAccount(accountId)
        if (updateResult != 1) {
            error("Couldn't deactivate account with id $accountId")
        }
    }

    fun validateAndGetAccount(
        accountId: Int,
    ): Account {
        val account = accountRepository
            .findById(accountId)
            .orElseThrow { NotFoundException("Account with id: $accountId not found") }
        if (!account.isActive) {
            throw InactiveAccountException("Account with id $accountId is inactive")
        }
        return account
    }
}
