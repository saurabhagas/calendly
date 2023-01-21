package com.harbor.calendly.service

import com.harbor.calendly.dto.AvailabilityDTO
import com.harbor.calendly.dto.MeetingDTO
import com.harbor.calendly.entity.Availability
import com.harbor.calendly.entity.Meeting
import com.harbor.calendly.entity.MeetingLink
import com.harbor.calendly.exception.NotFoundException
import com.harbor.calendly.repository.MeetingRepository
import com.harbor.calendly.utils.minsTill
import com.harbor.calendly.utils.toMeeting
import com.harbor.calendly.utils.toMeetingDTO
import mu.KLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MeetingService(
    val meetingRepository: MeetingRepository,
    val availabilityService: AvailabilityService,
    val meetingLinkService: MeetingLinkService
) {
    companion object : KLogging()

    fun createMeeting(
        meetingDTO: MeetingDTO
    ): Int {
        logger.info("createMeeting called with: {}", meetingDTO)
        require(meetingDTO.endTime > meetingDTO.startTime) {
            "End time should be greater than start time"
        }
        val meetingLink = meetingLinkService.validateAndGetMeetingLink(meetingDTO.meetingLinkId)
        logger.info("fetched meeting link: {}", meetingLink)

        require(meetingLink.durationInMins == meetingDTO.startTime.minsTill(meetingDTO.endTime)) {
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
        availabilityService.deleteAvailability(availability.id!!)
        if (meetingDTO.startTime == availability.startTime && meetingDTO.endTime < availability.endTime) {
            val newAvailability = AvailabilityDTO(
                accountId = availability.account.id!!,
                dayOfWeek = availability.dayOfWeek,
                startTime = meetingDTO.endTime,
                endTime = availability.endTime
            )
            availabilityService.createAvailability(newAvailability)
        } else if (meetingDTO.startTime > availability.startTime && meetingDTO.endTime == availability.endTime) {
            val newAvailability = AvailabilityDTO(
                accountId = availability.account.id!!,
                dayOfWeek = availability.dayOfWeek,
                startTime = availability.startTime,
                endTime = meetingDTO.startTime
            )
            availabilityService.createAvailability(newAvailability)
        } else if (meetingDTO.startTime > availability.startTime && meetingDTO.endTime < availability.endTime) {
            val newAvailability1 = AvailabilityDTO(
                accountId = availability.account.id!!,
                dayOfWeek = availability.dayOfWeek,
                startTime = availability.startTime,
                endTime = meetingDTO.startTime
            )
            availabilityService.createAvailability(newAvailability1)

            val newAvailability2 = AvailabilityDTO(
                accountId = availability.account.id,
                dayOfWeek = availability.dayOfWeek,
                startTime = meetingDTO.endTime,
                endTime = availability.endTime
            )
            availabilityService.createAvailability(newAvailability2)
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
        val meetingLink = meetingLinkService.validateAndGetMeetingLink(meetingDTO.meetingLinkId)
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
}
