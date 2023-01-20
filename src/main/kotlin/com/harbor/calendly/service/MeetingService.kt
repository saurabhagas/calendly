package com.harbor.calendly.service

import com.harbor.calendly.dto.MeetingDTO
import com.harbor.calendly.entity.Availability
import com.harbor.calendly.entity.Meeting
import com.harbor.calendly.entity.MeetingLink
import com.harbor.calendly.exception.NotFoundException
import com.harbor.calendly.repository.AvailabilityRepository
import com.harbor.calendly.repository.MeetingLinkRepository
import com.harbor.calendly.repository.MeetingRepository
import com.harbor.calendly.utils.toMeeting
import com.harbor.calendly.utils.toMeetingDTO
import mu.KLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.temporal.ChronoUnit

@Service
class MeetingService(
    val meetingRepository: MeetingRepository,
    val availabilityRepository: AvailabilityRepository,
    val meetingLinkRepository: MeetingLinkRepository
) {
    companion object : KLogging()

    fun createMeeting(
        meetingDTO: MeetingDTO
    ): Int {
        logger.info("createMeeting called with: {}", meetingDTO)
        require(meetingDTO.endTime > meetingDTO.startTime) {
            "End time should be greater than start time"
        }
        val meetingLink = validateAndGetMeetingLink(meetingDTO.meetingLinkId)
        logger.info("fetched meeting link: {}", meetingLink)

        require(meetingLink.durationInMins.toLong() == meetingDTO.startTime.until(meetingDTO.endTime, ChronoUnit.MINUTES)) {
            "Meeting link duration doesn't allow requested duration"
        }
        val availability = meetingLink.account.availabilities
            .filter { meetingDTO.date.dayOfWeek == it.dayOfWeek }
            .firstOrNull { meetingDTO.startTime >= it.startTime && meetingDTO.endTime <= it.endTime }
            ?: throw NotFoundException("Didn't find any matching slots in host's availability")

        val result = updateDb(availability, meetingDTO, meetingLink)
        logger.info("create result: {}", result)
        return requireNotNull(result.id)
    }

    @Transactional
    fun updateDb(
        availability: Availability,
        meetingDTO: MeetingDTO,
        meetingLink: MeetingLink,
    ): Meeting {
        updateAvailability(availability, meetingDTO)
        return meetingRepository.save(meetingDTO.toMeeting(meetingLink, null))
    }

    private fun updateAvailability(
        availability: Availability,
        meetingDTO: MeetingDTO,
    ) {
        availabilityRepository.deleteById(availability.id!!)
        if (meetingDTO.startTime == availability.startTime && meetingDTO.endTime < availability.endTime) {
            val newAvailability = availability.copy(
                id = null,
                startTime = meetingDTO.endTime,
                endTime = availability.endTime
            )
            availabilityRepository.save(newAvailability)
        } else if (meetingDTO.startTime > availability.startTime && meetingDTO.endTime == availability.endTime) {
            val newAvailability = availability.copy(
                id = null,
                startTime = availability.startTime,
                endTime = meetingDTO.startTime
            )
            availabilityRepository.save(newAvailability)
        } else if (meetingDTO.startTime > availability.startTime && meetingDTO.endTime < availability.endTime) {
            val newAvailability1 = availability.copy(
                id = null,
                startTime = availability.startTime,
                endTime = meetingDTO.startTime
            )
            availabilityRepository.save(newAvailability1)

            val newAvailability2 = availability.copy(
                id = null,
                startTime = meetingDTO.endTime,
                endTime = availability.endTime
            )
            availabilityRepository.save(newAvailability2)
        }
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
        meetingId: Int,
        meetingDTO: MeetingDTO
    ) {
        logger.info("updateMeeting called with: {} {}", meetingId, meetingDTO)
        require(meetingDTO.endTime > meetingDTO.startTime) {
            "End time should be greater than start time"
        }
        val meetingLink = validateAndGetMeetingLink(meetingDTO.meetingLinkId)
        logger.info("fetched meeting link: {}", meetingLink)
        val meeting = validateAndGetMeeting(meetingId)
        logger.info("fetched meeting: {}", meeting)
        meetingRepository.save(meetingDTO.toMeeting(meetingLink, meetingId))
    }

    fun deleteMeeting(meetingId: Int) {
        logger.info("deleteMeeting called with: {}", meetingId)
        val meeting = validateAndGetMeeting(meetingId)
        logger.info("fetched meeting: {}", meeting)
        meetingRepository.deleteById(meetingId)
    }

    private fun validateAndGetMeeting(
        meetingId: Int,
    ) = meetingRepository
        .findById(meetingId)
        .orElseThrow { NotFoundException("Meeting with id: $meetingId not found") }

    private fun validateAndGetMeetingLink(
        meetingLinkId: Int,
    ) = meetingLinkRepository
        .findById(meetingLinkId)
        .orElseThrow { NotFoundException("Meeting link with id: $meetingLinkId not found") }
}
