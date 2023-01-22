package com.harbor.calendly.service

import com.harbor.calendly.dto.AvailabilityDTO
import com.harbor.calendly.dto.MeetingDTO
import com.harbor.calendly.entity.AvailabilityEntity
import com.harbor.calendly.utils.firstOverlappingAvailability
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AvailabilitySplitterService(
    val availabilityService: AvailabilityService
) {
    @Transactional
    fun findOverlapAndUpdateAvailability(
        availabilities: List<AvailabilityEntity>,
        meetingDTO: MeetingDTO,
    ) {
        val availability = firstOverlappingAvailability(availabilities, meetingDTO)
        val accountId = availability.account.id!!

        availabilityService.deleteAvailability(accountId, availability.id!!)
        if (meetingDTO.startTime == availability.startTime && meetingDTO.endTime < availability.endTime) {
            val newAvailability = AvailabilityDTO(
                dayOfWeek = availability.dayOfWeek,
                startTime = meetingDTO.endTime,
                endTime = availability.endTime
            )
            availabilityService.createAvailability(accountId, newAvailability)
        } else if (meetingDTO.startTime > availability.startTime && meetingDTO.endTime == availability.endTime) {
            val newAvailability = AvailabilityDTO(
                dayOfWeek = availability.dayOfWeek,
                startTime = availability.startTime,
                endTime = meetingDTO.startTime
            )
            availabilityService.createAvailability(accountId, newAvailability)
        } else if (meetingDTO.startTime > availability.startTime && meetingDTO.endTime < availability.endTime) {
            val newAvailability1 = AvailabilityDTO(
                dayOfWeek = availability.dayOfWeek,
                startTime = availability.startTime,
                endTime = meetingDTO.startTime
            )
            availabilityService.createAvailability(accountId, newAvailability1)

            val newAvailability2 = AvailabilityDTO(
                dayOfWeek = availability.dayOfWeek,
                startTime = meetingDTO.endTime,
                endTime = availability.endTime
            )
            availabilityService.createAvailability(accountId, newAvailability2)
        }
    }
}
