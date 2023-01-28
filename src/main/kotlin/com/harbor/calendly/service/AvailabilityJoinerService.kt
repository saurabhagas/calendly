package com.harbor.calendly.service

import com.harbor.calendly.dto.AvailabilityDTO
import com.harbor.calendly.entity.AccountEntity
import com.harbor.calendly.entity.AvailabilityEntity
import com.harbor.calendly.repository.AvailabilityRepository
import com.harbor.calendly.utils.logger
import com.harbor.calendly.utils.toAvailability
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AvailabilityJoinerService(
    val availabilityRepository: AvailabilityRepository
) {
    @Transactional
    fun mergeOverlappingAvailabilities(
        account: AccountEntity,
        dto: AvailabilityDTO
    ): AvailabilityEntity {
        logger.info("mergeOverlappingAvailabilities called with $account and $dto")
        val availabilities = account.availabilities
        val overlappingAvailability = availabilities
            .filter { dto.dayOfWeek == it.dayOfWeek }
            .firstOrNull { dto.endTime == it.startTime || dto.startTime == it.endTime }
        logger.info("overlappingAvailability: $overlappingAvailability")

        return if (overlappingAvailability != null) {
            account.availabilities.remove(overlappingAvailability)
            availabilityRepository.delete(overlappingAvailability)
            val mergedAvailability = overlappingAvailability.copy(
                startTime = minOf(overlappingAvailability.startTime, dto.startTime),
                endTime = maxOf(overlappingAvailability.endTime, dto.endTime)
            )
            logger.info("Deleted $overlappingAvailability. Creating $mergedAvailability")
            availabilityRepository.save(mergedAvailability)
        } else {
            availabilityRepository.save(dto.toAvailability(account))
        }
    }
}
