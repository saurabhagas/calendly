package com.harbor.calendly.controller

import com.harbor.calendly.dto.AvailabilityDTO
import com.harbor.calendly.service.OverlapService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class OverlapController(val overlapService: OverlapService) {
    @GetMapping("${Endpoints.OVERLAP_URL}/{requesterAccountId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    fun getAvailabilities(
        @PathVariable("accountId") hostAccountId: String,
        @PathVariable("requesterAccountId") requesterAccountId: String
    ): List<AvailabilityDTO> = overlapService.getOverlaps(hostAccountId.toInt(), requesterAccountId.toInt())
}
