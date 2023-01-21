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
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/availability")
class AvailabilityController(val availabilityService: AvailabilityService) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createAvailability(
        @Valid @RequestBody availabilityDTO: AvailabilityDTO
    ) = availabilityService.createAvailability(availabilityDTO)

    @GetMapping("/{availabilityId}")
    @ResponseStatus(HttpStatus.OK)
    fun getAvailability(
        @PathVariable("availabilityId") availabilityId: String
    ) = availabilityService.getAvailability(availabilityId.toInt())

    @PutMapping("/{availabilityId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateAvailability(
        @Valid @RequestBody availabilityDTO: AvailabilityDTO,
        @PathVariable("availabilityId") availabilityId : Int
    ) = availabilityService.updateAvailability(availabilityId, availabilityDTO)

    @DeleteMapping("/{availabilityId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteAvailability(
        @PathVariable("availabilityId") availabilityId: String
    ) = availabilityService.deleteAvailability(availabilityId.toInt())
}
