package com.harbor.calendly.controller

import com.harbor.calendly.dto.MeetingDTO
import com.harbor.calendly.service.MeetingService
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
class MeetingController(val meetingService: MeetingService) {
    @RequestMapping(Endpoints.MEETINGS_URL, consumes = [MediaType.APPLICATION_JSON_VALUE], method = [RequestMethod.POST])
    @ResponseStatus(HttpStatus.CREATED)
    fun createMeeting(
        @PathVariable("meetingLinkId") meetingLinkId: String,
        @Valid @RequestBody meetingDTO: MeetingDTO
    ): Int = meetingService.createMeeting(meetingLinkId.toInt(), meetingDTO)

    @RequestMapping("${Endpoints.MEETINGS_URL}/{meetingId}", produces = [MediaType.APPLICATION_JSON_VALUE], method = [RequestMethod.GET])
    @ResponseStatus(HttpStatus.OK)
    fun getMeeting(
        @PathVariable("meetingId") meetingId: String
    ): MeetingDTO = meetingService.getMeeting(meetingId.toInt())

    @RequestMapping("${Endpoints.MEETINGS_URL}/{meetingId}", consumes = [MediaType.APPLICATION_JSON_VALUE], method = [RequestMethod.PUT])
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateMeeting(
        @PathVariable("meetingLinkId") meetingLinkId: String,
        @Valid @RequestBody meetingDTO: MeetingDTO,
        @PathVariable("meetingId") meetingId : Int
    ): Unit = meetingService.updateMeeting(meetingLinkId.toInt(), meetingId, meetingDTO)

    @RequestMapping("${Endpoints.MEETINGS_URL}/{meetingId}", method = [RequestMethod.DELETE])
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteMeeting(
        @PathVariable("meetingId") meetingId: String
    ): Unit = meetingService.deleteMeeting(meetingId.toInt())
}
