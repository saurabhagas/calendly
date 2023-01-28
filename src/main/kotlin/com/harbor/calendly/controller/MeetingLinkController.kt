package com.harbor.calendly.controller

import com.harbor.calendly.dto.MeetingLinkDTO
import com.harbor.calendly.service.MeetingLinkService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
class MeetingLinkController(val meetingLinkService: MeetingLinkService) {
    @RequestMapping(Endpoints.MEETING_LINKS_URL, consumes = [MediaType.APPLICATION_JSON_VALUE], method = [RequestMethod.POST])
    @ResponseStatus(HttpStatus.CREATED)
    fun createMeetingLink(
        @PathVariable("accountId") accountId: String,
        @Valid @RequestBody meetingLinkDTO: MeetingLinkDTO
    ): Int = meetingLinkService.createMeetingLink(accountId.toInt(), meetingLinkDTO)

    @RequestMapping(Endpoints.MEETING_LINKS_URL, produces = [MediaType.APPLICATION_JSON_VALUE], method = [RequestMethod.GET])
    @ResponseStatus(HttpStatus.OK)
    fun getAllMeetingLinks(
        @PathVariable("accountId") accountId: String
    ): List<MeetingLinkDTO> = meetingLinkService.getAllMeetingLinks(accountId.toInt())

    @RequestMapping("${Endpoints.MEETING_LINKS_URL}/{meetingLinkId}", produces = [MediaType.APPLICATION_JSON_VALUE], method = [RequestMethod.GET])
    @ResponseStatus(HttpStatus.OK)
    fun getMeetingLink(
        @PathVariable("meetingLinkId") meetingLinkId: String
    ): MeetingLinkDTO = meetingLinkService.getMeetingLink(meetingLinkId.toInt())

    @RequestMapping("${Endpoints.MEETING_LINKS_URL}/{meetingLinkId}", consumes = [MediaType.APPLICATION_JSON_VALUE], method = [RequestMethod.PUT])
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateMeetingLink(
        @PathVariable("accountId") accountId: String,
        @Valid @RequestBody meetingLinkDTO: MeetingLinkDTO,
        @PathVariable("meetingLinkId") meetingLinkId : Int
    ): Unit = meetingLinkService.updateMeetingLink(accountId.toInt(), meetingLinkId, meetingLinkDTO)

    @RequestMapping("${Endpoints.MEETING_LINKS_URL}/{meetingLinkId}", method = [RequestMethod.DELETE])
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteMeetingLink(
        @PathVariable("meetingLinkId") meetingLinkId: String
    ): Unit = meetingLinkService.deleteMeetingLink(meetingLinkId.toInt())
}
