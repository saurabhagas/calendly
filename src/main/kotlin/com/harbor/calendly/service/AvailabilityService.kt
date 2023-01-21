package com.harbor.calendly.service

import com.harbor.calendly.dto.AvailabilityDTO
import com.harbor.calendly.entity.Availability
import com.harbor.calendly.exception.NotFoundException
import com.harbor.calendly.repository.AvailabilityRepository
import com.harbor.calendly.utils.toAvailability
import com.harbor.calendly.utils.toAvailabilityDTO
import mu.KLogging
import org.springframework.stereotype.Service

@Service
class AvailabilityService(
    val accountService: AccountService,
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
        val account = accountService.validateAndGetAccount(availabilityDTO.accountId)
        checkForOverlappingAvailabilities(account.availabilities, availabilityDTO)
        logger.info("fetched account: {}", account)
        val result = availabilityRepository.save(availabilityDTO.toAvailability(account))
        logger.info("create result: {}", result)
        return requireNotNull(result.id)
    }

    private fun checkForOverlappingAvailabilities(
        availabilities: List<Availability>,
        dto: AvailabilityDTO
    ) {
        val overlappingAvailabilities = availabilities
            .filter { dto.dayOfWeek == it.dayOfWeek }
            .filterNot { dto.endTime <= it.startTime || dto.startTime >= it.endTime }
        require(overlappingAvailabilities.isEmpty()) {
            "Overlapping availabilities $overlappingAvailabilities already exist"
        }
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
        val account = accountService.validateAndGetAccount(availabilityDTO.accountId)
        logger.info("fetched account: {}", account)
        val availability = validateAndGetAvailability(availabilityId)
        logger.info("fetched availability: {}", availability)
        availabilityRepository.save(availabilityDTO.toAvailability(account, availabilityId))
    }

    fun deleteAvailability(availabilityId: Int) {
        logger.info("deleteAvailability called with id: {}", availabilityId)
        val availability = validateAndGetAvailability(availabilityId)
        logger.info("fetched availability: {}", availability)
        val deletedRecords = availabilityRepository.deleteRecord(availabilityId)
        if (deletedRecords != 1) {
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
