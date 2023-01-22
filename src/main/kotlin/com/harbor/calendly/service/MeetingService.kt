package com.harbor.calendly.service

import com.harbor.calendly.dto.MeetingDTO
import com.harbor.calendly.entity.MeetingEntity
import com.harbor.calendly.entity.MeetingLinkEntity
import com.harbor.calendly.exception.NotFoundException
import com.harbor.calendly.repository.MeetingRepository
import com.harbor.calendly.utils.minsTill
import com.harbor.calendly.utils.toMeeting
import com.harbor.calendly.utils.toMeetingDTO
import mu.KLogging
import org.springframework.stereotype.Service

@Service
class MeetingService(
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
        val result = updateDb(meetingDTO, meetingLink)
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
        val meetingLink = meetingLinkService.validateAndGetMeetingLink(meetingLinkId)
        logger.info("fetched meeting link: {}", meetingLink)
        val meeting = validateAndGetMeeting(meetingId)
        logger.info("fetched meeting: {}", meeting)
        meetingRepository.save(meetingDTO.toMeeting(meetingLink, meetingId))
    }

    fun deleteMeeting(meetingId: Int) {
        logger.info("deleteMeeting called with: {}", meetingId)
        val meeting = validateAndGetMeeting(meetingId)
        logger.info("fetched meeting: {}", meeting)
        meetingLinkService.validateAndGetMeetingLink(meeting.meetingLink.id!!).meetings.remove(meeting)
        meetingRepository.deleteById(meetingId)
        if (meetingRepository.findById(meetingId).isPresent) {
            error("Couldn't delete meeting link with id: $meetingId")
        }
    }

    private fun updateDb(
        meetingDTO: MeetingDTO,
        meetingLink: MeetingLinkEntity,
    ): MeetingEntity {
        val availabilities = meetingLink.account.availabilities
        availabilityUpdaterService.findOverlapAndUpdateAvailability(availabilities, meetingDTO)
        return meetingRepository.save(meetingDTO.toMeeting(meetingLink, null))
    }

    private fun validateAndGetMeeting(
        meetingId: Int,
    ) = meetingRepository
        .findById(meetingId)
        .orElseThrow { NotFoundException("Meeting with id: $meetingId not found") }
}
