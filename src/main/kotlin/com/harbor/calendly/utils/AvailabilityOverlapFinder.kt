package com.harbor.calendly.utils

import com.harbor.calendly.dto.AvailabilityDTO
import com.harbor.calendly.dto.MeetingDTO
import com.harbor.calendly.entity.AvailabilityEntity
import com.harbor.calendly.exception.NotFoundException
import mu.KLogging

val logger = KLogging().logger()

//TODO[Saurabh]: Write unit tests for this file
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
    hostAvailabilities: List<AvailabilityEntity>,
    requesterAvailabilities: List<AvailabilityEntity>,
): List<AvailabilityDTO> {
    logger.info("findAllOverlaps called with $hostAvailabilities and $requesterAvailabilities")
    val compareByDayAndStartTime = Comparator.comparing(AvailabilityEntity::dayOfWeek)
        .thenComparing(AvailabilityEntity::startTime)
    val mergedAndSorted = hostAvailabilities.plus(requesterAvailabilities).sortedWith(compareByDayAndStartTime)
    logger.info("Merged and sorted availabilities: $mergedAndSorted")

    // There's no need to merge the overlapping availabilities because conjunction of two sets of disjoint availabilities cannot intersect
    return (1 until mergedAndSorted.size)
        .filter { index -> doesCurrentOverlapWithPrevious(mergedAndSorted, index) }
        .map { index ->
            AvailabilityDTO(
                dayOfWeek = mergedAndSorted[index].dayOfWeek,
                startTime = mergedAndSorted[index].startTime,
                endTime = mergedAndSorted[index - 1].endTime,
            )
        }
        .also { logger.info("Overlapping availabilities: $it") }
}

private fun doesCurrentOverlapWithPrevious(
    availabilities: List<AvailabilityEntity>,
    index: Int,
) = availabilities[index - 1].dayOfWeek == availabilities[index].dayOfWeek &&
    availabilities[index].startTime < availabilities[index - 1].endTime
