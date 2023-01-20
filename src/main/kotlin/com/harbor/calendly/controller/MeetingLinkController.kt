package com.harbor.calendly.controller

import com.harbor.calendly.dto.MeetingLinkDTO
import com.harbor.calendly.service.MeetingLinkService
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
@RequestMapping("/meeting-links")
class MeetingLinkController(val meetingLinkService: MeetingLinkService) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createMeetingLink(
        @Valid @RequestBody meetingLinkDTO: MeetingLinkDTO
    ) = meetingLinkService.createMeetingLink(meetingLinkDTO)

    @GetMapping("/{meetingLinkId}")
    @ResponseStatus(HttpStatus.OK)
    fun getMeetingLink(
        @PathVariable("meetingLinkId") meetingLinkId: String
    ) = meetingLinkService.getMeetingLink(meetingLinkId.toInt())

    @PutMapping("/{meetingLinkId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateMeetingLink(
        @RequestBody meetingLinkDTO: MeetingLinkDTO,
        @PathVariable("meetingLinkId") meetingLinkId : Int
    ) = meetingLinkService.updateMeetingLink(meetingLinkId, meetingLinkDTO)

    @DeleteMapping("/{meetingLinkId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteMeetingLink(
        @PathVariable("meetingLinkId") meetingLinkId: String
    ) = meetingLinkService.deleteMeetingLink(meetingLinkId.toInt())
}
