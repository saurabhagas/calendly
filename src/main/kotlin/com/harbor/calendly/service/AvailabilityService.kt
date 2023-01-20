package com.harbor.calendly.service

import com.harbor.calendly.dto.AvailabilityDTO
import com.harbor.calendly.exception.NotFoundException
import com.harbor.calendly.repository.AccountRepository
import com.harbor.calendly.repository.AvailabilityRepository
import com.harbor.calendly.utils.toAvailability
import com.harbor.calendly.utils.toAvailabilityDTO
import mu.KLogging
import org.springframework.stereotype.Service
import java.time.DayOfWeek

@Service
class AvailabilityService(
    val accountRepository: AccountRepository,
    val availabilityRepository: AvailabilityRepository
) {
    companion object : KLogging()

    fun createAvailability(
        availabilityDTO: AvailabilityDTO
    ): Int {
        logger.info("createAvailability called with: {}", availabilityDTO)
        require(availabilityDTO.endTime > availabilityDTO.startTime) {
            "End time should be greater than start time"
        }
        val account = validateAndGetAccount(availabilityDTO.accountId)
        validateExistingAvailability(account.id!!, availabilityDTO.dayOfWeek)
        logger.info("fetched account: {}", account)
        val result = availabilityRepository.save(availabilityDTO.toAvailability(account))
        logger.info("create result: {}", result)
        return requireNotNull(result.id)
    }

    private fun validateExistingAvailability(
        accountId: Int,
        dayOfWeek: DayOfWeek,
    ) = require(availabilityRepository.findByAccountAndDayOfWeek(accountId, dayOfWeek.ordinal) == null) {
        "An availability already exists for accountId: $accountId and dayOfWeek: $dayOfWeek"
    }

    fun getAvailability(
        availabilityId: Int
    ): AvailabilityDTO {
        logger.info("getAvailability called with: {}", availabilityId)
        val availability = validateAndGetAvailability(availabilityId)
        logger.info("fetched availability: {}", availability)
        return availability.toAvailabilityDTO()
    }

    fun updateAvailability(
        availabilityId: Int,
        availabilityDTO: AvailabilityDTO
    ) {
        logger.info("updateAvailability called with: {} {}", availabilityId, availabilityDTO)
        require(availabilityDTO.endTime > availabilityDTO.startTime) {
            "End time should be greater than start time"
        }
        val account = validateAndGetAccount(availabilityDTO.accountId)
        logger.info("fetched account: {}", account)
        val availability = validateAndGetAvailability(availabilityId)
        logger.info("fetched availability: {}", availability)
        availabilityRepository.save(availabilityDTO.toAvailability(account, availabilityId))
    }

    fun deleteAvailability(availabilityId: Int) {
        logger.info("deleteAvailability called with: {}", availabilityId)
        val availability = validateAndGetAvailability(availabilityId)
        logger.info("fetched availability: {}", availability)
        availabilityRepository.deleteById(availabilityId)
    }

    private fun validateAndGetAvailability(
        availabilityId: Int,
    ) = availabilityRepository
        .findById(availabilityId)
        .orElseThrow { NotFoundException("Availability with id: $availabilityId not found") }

    private fun validateAndGetAccount(
        accountId: Int,
    ) = accountRepository
        .findById(accountId)
        .orElseThrow { NotFoundException("Account with id: $accountId not found") }
}
