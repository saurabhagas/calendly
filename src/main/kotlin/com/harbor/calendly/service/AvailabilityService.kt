package com.harbor.calendly.service

import com.harbor.calendly.dto.AvailabilityDTO
import com.harbor.calendly.exception.NotFoundException
import com.harbor.calendly.repository.AvailabilityRepository
import com.harbor.calendly.utils.checkForOverlappingAvailabilities
import com.harbor.calendly.utils.toAvailability
import com.harbor.calendly.utils.toAvailabilityDTO
import mu.KLogging
import org.springframework.stereotype.Service

@Service
class AvailabilityService(
    val accountService: AccountService,
    val availabilityJoinerService: AvailabilityJoinerService,
    val availabilityRepository: AvailabilityRepository
) {
    companion object : KLogging()

    fun createAvailability(
        accountId: Int,
        availabilityDTO: AvailabilityDTO
    ): Int {
        logger.info("createAvailability called with accountId: {} and {}", accountId, availabilityDTO)
        require(availabilityDTO.endTime > availabilityDTO.startTime) {
            "End time should be greater than start time"
        }
        val account = accountService.validateAndGetAccount(accountId)
        logger.info("fetched account: {}", account)
        checkForOverlappingAvailabilities(account.availabilities, availabilityDTO)
        val result = availabilityJoinerService.mergeOverlappingAvailabilities(account, availabilityDTO)
        logger.info("create result: {}", result)
        return requireNotNull(result.id)
    }

    fun getAvailability(
        availabilityId: Int
    ): AvailabilityDTO {
        logger.info("getAvailability called with: {}", availabilityId)
        val availability = validateAndGetAvailability(availabilityId)
        logger.info("fetched availability: {}", availability)
        return availability.toAvailabilityDTO()
    }

    fun getAvailabilities(
        accountId: Int,
    ): List<AvailabilityDTO> {
        logger.info("getAvailabilities called with accountId: {}", accountId)
        val account = accountService.validateAndGetAccount(accountId)
        logger.info("fetched account: {}", account)
        return account.availabilities.map { it.toAvailabilityDTO() }
    }

    fun updateAvailability(
        accountId: Int,
        availabilityId: Int,
        availabilityDTO: AvailabilityDTO
    ) {
        logger.info("updateAvailability called with accountId: {}, availabilityId: {} and {}", availabilityId, availabilityId, availabilityDTO)
        require(availabilityDTO.endTime > availabilityDTO.startTime) {
            "End time should be greater than start time"
        }
        val account = accountService.validateAndGetAccount(accountId)
        logger.info("fetched account: {}", account)
        val availability = validateAndGetAvailability(availabilityId)
        logger.info("fetched availability: {}", availability)
        availabilityRepository.save(availabilityDTO.toAvailability(account, availabilityId))
    }

    fun deleteAvailability(
        accountId: Int,
        availabilityId: Int
    ) {
        logger.info("deleteAvailability called with accountId: {}, availabilityId: {}", accountId, availabilityId)
        val availability = validateAndGetAvailability(availabilityId)
        logger.info("fetched availability: {}", availability)
        accountService.validateAndGetAccount(accountId).availabilities.remove(availability)

        availabilityRepository.deleteById(availabilityId)
        if (availabilityRepository.findById(availabilityId).isPresent) {
          error("Couldn't delete availability with id: $availabilityId")
        }
        logger.info("Deleted availability with id: {}", availabilityId)
    }

    fun validateAndGetAvailability(
        availabilityId: Int,
    ) = availabilityRepository
        .findById(availabilityId)
        .orElseThrow { NotFoundException("Availability with id: $availabilityId not found") }
}
