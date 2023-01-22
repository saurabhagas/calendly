package com.harbor.calendly.controller

import com.harbor.calendly.dto.AvailabilityDTO
import com.harbor.calendly.service.AvailabilityService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
class AvailabilityController(val availabilityService: AvailabilityService) {
    @PostMapping("/accounts/{accountId}/availability")
    @ResponseStatus(HttpStatus.CREATED)
    fun createAvailability(
        @PathVariable("accountId") accountId: String,
        @Valid @RequestBody availabilityDTO: AvailabilityDTO
    ) = availabilityService.createAvailability(accountId.toInt(), availabilityDTO)

    @GetMapping("/accounts/{accountId}/availability")
    @ResponseStatus(HttpStatus.OK)
    fun getAvailabilities(
        @PathVariable("accountId") accountId: String,
    ) = availabilityService.getAvailabilities(accountId.toInt())

    @GetMapping("/accounts/{accountId}/availability/{availabilityId}")
    @ResponseStatus(HttpStatus.OK)
    fun getAvailability(
        @PathVariable("availabilityId") availabilityId: String
    ) = availabilityService.getAvailability(availabilityId.toInt())

    @PutMapping("/accounts/{accountId}/availability/{availabilityId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateAvailability(
        @PathVariable("accountId") accountId: String,
        @Valid @RequestBody availabilityDTO: AvailabilityDTO,
        @PathVariable("availabilityId") availabilityId : Int
    ) = availabilityService.updateAvailability(accountId.toInt(), availabilityId, availabilityDTO)

    @DeleteMapping("/accounts/{accountId}/availability/{availabilityId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteAvailability(
        @PathVariable("accountId") accountId: String,
        @PathVariable("availabilityId") availabilityId: String
    ) = availabilityService.deleteAvailability(accountId.toInt(), availabilityId.toInt())
}
