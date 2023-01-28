package com.harbor.calendly.service

import com.harbor.calendly.dto.AvailabilityDTO
import com.harbor.calendly.utils.findAllOverlaps
import org.springframework.stereotype.Service

@Service
class OverlapService(
    val accountService: AccountService
) {
    fun getOverlaps(
        hostAccountId: Int,
        requesterAccountId: Int
    ): List<AvailabilityDTO> {
        val hostAvailabilities = accountService.validateAndGetAccount(hostAccountId).availabilities
        val requesterAvailabilities = accountService.validateAndGetAccount(requesterAccountId).availabilities
        return findAllOverlaps(hostAvailabilities, requesterAvailabilities)
    }
}
