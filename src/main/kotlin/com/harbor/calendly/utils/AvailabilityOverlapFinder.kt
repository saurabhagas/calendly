package com.harbor.calendly.utils

import com.harbor.calendly.dto.AvailabilityDTO
import com.harbor.calendly.dto.MeetingDTO
import com.harbor.calendly.entity.AvailabilityEntity
import com.harbor.calendly.exception.NotFoundException
import mu.KLogging

val logger = KLogging().logger()

fun checkForOverlappingAvailabilities(
    availabilities: List<AvailabilityEntity>,
    availabilityDTO: AvailabilityDTO,
) {
    logger.info("checkForOverlappingAvailabilities called with $availabilities and $availabilityDTO")
    val overlappingAvailabilities = availabilities
        .filter { availabilityDTO.dayOfWeek == it.dayOfWeek }
        .filterNot { availabilityDTO.endTime <= it.startTime || availabilityDTO.startTime >= it.endTime }
    require(overlappingAvailabilities.isEmpty()) {
        "Overlapping availabilities $overlappingAvailabilities already exist"
    }
}

fun firstOverlappingAvailability(
    availabilities: List<AvailabilityEntity>,
    meetingDTO: MeetingDTO,
): AvailabilityEntity {
    logger.info("checkForOverlappingAvailabilities called with $availabilities and $meetingDTO")
    return availabilities
        .filter { meetingDTO.date.dayOfWeek == it.dayOfWeek }
        .firstOrNull { meetingDTO.startTime >= it.startTime && meetingDTO.endTime <= it.endTime }
        ?: throw NotFoundException("Didn't find any matching slots in host's availability")
}
