package com.harbor.calendly.service

import com.harbor.calendly.dto.MeetingDTO
import com.harbor.calendly.exception.NotFoundException
import com.harbor.calendly.repository.MeetingRepository
import com.harbor.calendly.utils.minsTill
import com.harbor.calendly.utils.toMeeting
import com.harbor.calendly.utils.toMeetingDTO
import mu.KLogging
import org.springframework.stereotype.Service

@Service
class MeetingService(
    val accountService: AccountService,
    val meetingRepository: MeetingRepository,
    val meetingLinkService: MeetingLinkService,
    val availabilityUpdaterService: AvailabilitySplitterService,
) {
    companion object : KLogging()

    fun createMeeting(
        meetingLinkId: Int,
        meetingDTO: MeetingDTO
    ): Int {
        logger.info("createMeeting called with meetingLinkId: {} and {}", meetingLinkId, meetingDTO)
        require(meetingDTO.endTime > meetingDTO.startTime) {
            "End time should be greater than start time"
        }
        val meetingLink = meetingLinkService.validateAndGetMeetingLink(meetingLinkId)
        logger.info("fetched meeting link: {}", meetingLink)

        require(meetingLink.durationInMins == meetingDTO.startTime.minsTill(meetingDTO.endTime)) {
            "Meeting link duration doesn't allow requested duration"
        }
        val requesterAccount = accountService.validateAndGetAccount(meetingDTO.requesterAccountId)

        val hostAvailabilities = meetingLink.account.availabilities
        val requesterAvailabilities = requesterAccount.availabilities
        availabilityUpdaterService.checkAndUpdateAvailability(hostAvailabilities, requesterAvailabilities, meetingDTO)
        val result = meetingRepository.save(meetingDTO.toMeeting(meetingLink, null, requesterAccount))
        logger.info("create result: {}", result)
        return requireNotNull(result.id)
    }

    fun getMeeting(
        meetingId: Int
    ): MeetingDTO {
        logger.info("getMeeting called with: {}", meetingId)
        val meeting = validateAndGetMeeting(meetingId)
        logger.info("fetched meeting: {}", meeting)
        return meeting.toMeetingDTO()
    }

    fun updateMeeting(
        meetingLinkId: Int,
        meetingId: Int,
        meetingDTO: MeetingDTO
    ) {
        logger.info("updateMeeting called with meetingLinkId: {}, meetingId: {} and {}", meetingLinkId, meetingId, meetingDTO)
        require(meetingDTO.endTime > meetingDTO.startTime) {
            "End time should be greater than start time"
        }
        val requesterAccount = accountService.validateAndGetAccount(meetingDTO.requesterAccountId)
        val meetingLink = meetingLinkService.validateAndGetMeetingLink(meetingLinkId)
        logger.info("fetched meeting link: {}", meetingLink)
        val meeting = validateAndGetMeeting(meetingId)
        logger.info("fetched meeting: {}", meeting)
        meetingRepository.save(meetingDTO.toMeeting(meetingLink, meetingId, requesterAccount))
    }

    fun deleteMeeting(meetingId: Int) {
        logger.info("deleteMeeting called with: {}", meetingId)
        val meeting = validateAndGetMeeting(meetingId)
        logger.info("fetched meeting: {}", meeting)
        val meetingLink = meetingLinkService.validateAndGetMeetingLink(meeting.meetingLink.id!!)
        accountService.validateAndGetAccount(meetingLink.account.id!!).meetingsRequested.remove(meeting)
        meetingLink.meetings.remove(meeting)
        meetingRepository.deleteById(meetingId)
        if (meetingRepository.findById(meetingId).isPresent) {
            error("Couldn't delete meeting link with id: $meetingId")
        }
    }

    private fun validateAndGetMeeting(
        meetingId: Int,
    ) = meetingRepository
        .findById(meetingId)
        .orElseThrow { NotFoundException("Meeting with id: $meetingId not found") }
}
