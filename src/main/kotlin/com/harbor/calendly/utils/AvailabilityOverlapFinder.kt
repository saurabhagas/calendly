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
        ?: throw NotFoundException("Didn't find any matching slots in availability")
}

fun findAllOverlaps(
    hostAvailabilities: MutableList<AvailabilityEntity>,
    requesterAvailabilities: MutableList<AvailabilityEntity>,
): List<AvailabilityDTO> {
    logger.info("findAllOverlaps called with $hostAvailabilities and $requesterAvailabilities")
    val compareByDayAndStartTime = Comparator
        .comparing(AvailabilityEntity::dayOfWeek)
        .thenComparing(AvailabilityEntity::startTime)

    // Availabilities are sorted based on insertion time, NOT by starTime
    // Sort by startTime to get nlogn time instead of n^2 time in comparisons. sortWith() uses in-place sorting on mutable lists
    hostAvailabilities.sortWith(compareByDayAndStartTime)
    requesterAvailabilities.sortWith(compareByDayAndStartTime)

    var hostPointer = 0
    var requesterPointer = 0
    val result = mutableListOf<AvailabilityDTO>()
    while (hostPointer < hostAvailabilities.size && requesterPointer < requesterAvailabilities.size) {
        if (hasOverlap(hostAvailabilities, requesterAvailabilities, hostPointer, requesterPointer)) {
            result.add(
                AvailabilityDTO(
                    dayOfWeek = hostAvailabilities[hostPointer].dayOfWeek,
                    startTime = maxOf(requesterAvailabilities[requesterPointer].startTime, hostAvailabilities[hostPointer].startTime),
                    endTime = minOf(requesterAvailabilities[requesterPointer].endTime, hostAvailabilities[hostPointer].endTime),
                )
            )
            if (requesterAvailabilities[requesterPointer].endTime < hostAvailabilities[hostPointer].endTime) {
                // requester's availability is a subset of host's availability - increment fast pointer only
                requesterPointer++
            } else if (hostAvailabilities[hostPointer].endTime < requesterAvailabilities[requesterPointer].endTime) {
                // host's availability is a subset of requester's availability - increment fast pointer only
                hostPointer++
            } else {
                requesterPointer++
                hostPointer++
            }
        } else {
            hostPointer = requesterPointer
            requesterPointer++
        }
    }
    logger.info("Overlapping availabilities: $result")
    // There's no need to merge the overlapping availabilities because conjunction of two sets of disjoint availabilities cannot intersect
    return result
}

private fun hasOverlap(
    first: List<AvailabilityEntity>,
    second: List<AvailabilityEntity>,
    firstIndex: Int,
    secondIndex: Int,
) = first[firstIndex].dayOfWeek == second[secondIndex].dayOfWeek &&
    second[secondIndex].startTime < first[firstIndex].endTime
