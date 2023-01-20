package com.harbor.calendly.controller

import com.harbor.calendly.dto.MeetingDTO
import com.harbor.calendly.service.MeetingService
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
@RequestMapping("/meetings")
class MeetingController(val meetingService: MeetingService) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createMeeting(
        @Valid @RequestBody meetingDTO: MeetingDTO
    ) = meetingService.createMeeting(meetingDTO)

    @GetMapping("/{meetingId}")
    @ResponseStatus(HttpStatus.OK)
    fun getMeeting(
        @PathVariable("meetingId") meetingId: String
    ) = meetingService.getMeeting(meetingId.toInt())

    @PutMapping("/{meetingId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateMeeting(
        @RequestBody meetingDTO: MeetingDTO,
        @PathVariable("meetingId") meetingId : Int
    ) = meetingService.updateMeeting(meetingId, meetingDTO)

    @DeleteMapping("/{meetingId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteMeeting(
        @PathVariable("meetingId") meetingId: String
    ) = meetingService.deleteMeeting(meetingId.toInt())
}
