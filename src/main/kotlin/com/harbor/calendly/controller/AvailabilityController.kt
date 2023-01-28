package com.harbor.calendly.controller

import com.harbor.calendly.dto.AvailabilityDTO
import com.harbor.calendly.service.AvailabilityService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
class AvailabilityController(val availabilityService: AvailabilityService) {
    @PostMapping(Endpoints.AVAILABILITY_URL, consumes = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.CREATED)
    fun createAvailability(
        @PathVariable("accountId") accountId: String,
        @Valid @RequestBody availabilityDTO: AvailabilityDTO
    ): Int = availabilityService.createAvailability(accountId.toInt(), availabilityDTO)

    @GetMapping(Endpoints.AVAILABILITY_URL, produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    fun getAvailabilities(
        @PathVariable("accountId") accountId: String,
    ): List<AvailabilityDTO> = availabilityService.getAvailabilities(accountId.toInt())

    @GetMapping("${Endpoints.AVAILABILITY_URL}/{availabilityId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    fun getAvailability(
        @PathVariable("availabilityId") availabilityId: String
    ): AvailabilityDTO = availabilityService.getAvailability(availabilityId.toInt())

    @PutMapping("${Endpoints.AVAILABILITY_URL}/{availabilityId}", consumes = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateAvailability(
        @PathVariable("accountId") accountId: String,
        @Valid @RequestBody availabilityDTO: AvailabilityDTO,
        @PathVariable("availabilityId") availabilityId : Int
    ): Unit = availabilityService.updateAvailability(accountId.toInt(), availabilityId, availabilityDTO)

    @DeleteMapping("${Endpoints.AVAILABILITY_URL}/{availabilityId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteAvailability(
        @PathVariable("accountId") accountId: String,
        @PathVariable("availabilityId") availabilityId: String
    ) = availabilityService.deleteAvailability(accountId.toInt(), availabilityId.toInt())
}
