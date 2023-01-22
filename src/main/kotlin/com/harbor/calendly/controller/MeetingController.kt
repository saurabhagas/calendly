package com.harbor.calendly.controller

import com.harbor.calendly.dto.MeetingDTO
import com.harbor.calendly.service.MeetingService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
class MeetingController(val meetingService: MeetingService) {
    @RequestMapping("/accounts/{accountId}/meeting-links/{meetingLinkId}/meetings", method = [RequestMethod.POST])
    @ResponseStatus(HttpStatus.CREATED)
    fun createMeeting(
        @PathVariable("meetingLinkId") meetingLinkId: String,
        @Valid @RequestBody meetingDTO: MeetingDTO
    ) = meetingService.createMeeting(meetingLinkId.toInt(), meetingDTO)

    @RequestMapping("/accounts/{accountId}/meeting-links/{meetingLinkId}/meetings/{meetingId}", method = [RequestMethod.GET])
    @ResponseStatus(HttpStatus.OK)
    fun getMeeting(
        @PathVariable("meetingId") meetingId: String
    ) = meetingService.getMeeting(meetingId.toInt())

    @RequestMapping("/accounts/{accountId}/meeting-links/{meetingLinkId}/meetings/{meetingId}", method = [RequestMethod.PUT])
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateMeeting(
        @PathVariable("meetingLinkId") meetingLinkId: String,
        @Valid @RequestBody meetingDTO: MeetingDTO,
        @PathVariable("meetingId") meetingId : Int
    ) = meetingService.updateMeeting(meetingLinkId.toInt(), meetingId, meetingDTO)

    @RequestMapping("/accounts/{accountId}/meeting-links/{meetingLinkId}/meetings/{meetingId}", method = [RequestMethod.DELETE])
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteMeeting(
        @PathVariable("meetingId") meetingId: String
    ) = meetingService.deleteMeeting(meetingId.toInt())
}
