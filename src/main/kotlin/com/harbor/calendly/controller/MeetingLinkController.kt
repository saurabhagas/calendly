package com.harbor.calendly.controller

import com.harbor.calendly.dto.MeetingLinkDTO
import com.harbor.calendly.service.MeetingLinkService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
class MeetingLinkController(val meetingLinkService: MeetingLinkService) {
    @RequestMapping("/accounts/{accountId}/meeting-links", method = [RequestMethod.POST])
    @ResponseStatus(HttpStatus.CREATED)
    fun createMeetingLink(
        @PathVariable("accountId") accountId: String,
        @Valid @RequestBody meetingLinkDTO: MeetingLinkDTO
    ) = meetingLinkService.createMeetingLink(accountId.toInt(), meetingLinkDTO)

    @RequestMapping("/accounts/{accountId}/meeting-links", method = [RequestMethod.GET])
    @ResponseStatus(HttpStatus.OK)
    fun getAllMeetingLinks(
        @PathVariable("accountId") accountId: String
    ) = meetingLinkService.getAllMeetingLinks(accountId.toInt())

    @RequestMapping("/accounts/{accountId}/meeting-links/{meetingLinkId}", method = [RequestMethod.GET])
    @ResponseStatus(HttpStatus.OK)
    fun getMeetingLink(
        @PathVariable("meetingLinkId") meetingLinkId: String
    ) = meetingLinkService.getMeetingLink(meetingLinkId.toInt())

    @RequestMapping("/accounts/{accountId}/meeting-links/{meetingLinkId}", method = [RequestMethod.PUT])
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateMeetingLink(
        @PathVariable("accountId") accountId: String,
        @Valid @RequestBody meetingLinkDTO: MeetingLinkDTO,
        @PathVariable("meetingLinkId") meetingLinkId : Int
    ) = meetingLinkService.updateMeetingLink(accountId.toInt(), meetingLinkId, meetingLinkDTO)

    @RequestMapping("/accounts/{accountId}/meeting-links/{meetingLinkId}", method = [RequestMethod.DELETE])
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteMeetingLink(
        @PathVariable("meetingLinkId") meetingLinkId: String
    ) = meetingLinkService.deleteMeetingLink(meetingLinkId.toInt())
}
