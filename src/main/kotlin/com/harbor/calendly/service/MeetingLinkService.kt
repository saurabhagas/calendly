package com.harbor.calendly.service

import com.harbor.calendly.dto.MeetingLinkDTO
import com.harbor.calendly.entity.AccountEntity
import com.harbor.calendly.exception.NotFoundException
import com.harbor.calendly.repository.MeetingLinkRepository
import com.harbor.calendly.utils.minsTill
import com.harbor.calendly.utils.toMeetingLink
import com.harbor.calendly.utils.toMeetingLinkDTO
import mu.KLogging
import org.springframework.stereotype.Service

@Service
class MeetingLinkService(
    val accountService: AccountService,
    val meetingLinkRepository: MeetingLinkRepository
) {
    companion object : KLogging()

    fun createMeetingLink(
        accountId: Int,
        meetingLinkDTO: MeetingLinkDTO
    ): Int {
        logger.info("createMeetingLink called with accountId: {} and {}", accountId, meetingLinkDTO)
        val account = accountService.validateAndGetAccount(accountId)
        logger.info("fetched account: {}", account)
        ensureAvailability(account, meetingLinkDTO)
        val result = meetingLinkRepository.save(meetingLinkDTO.toMeetingLink(account))
        logger.info("create result: {}", result)
        return requireNotNull(result.id)
    }

    fun getMeetingLink(
        meetingLinkId: Int
    ): MeetingLinkDTO {
        logger.info("getMeetingLink called with: {}", meetingLinkId)
        val meetingLink = validateAndGetMeetingLink(meetingLinkId)
        logger.info("fetched meeting link: {}", meetingLink)
        return meetingLink.toMeetingLinkDTO()
    }

    fun getAllMeetingLinks(
        accountId: Int
    ): List<MeetingLinkDTO> {
        logger.info("getAllMeetingLinks called with: {}", accountId)
        val account = accountService.validateAndGetAccount(accountId)
        logger.info("fetched account: {}", account)
        return account.meetingLinks.map { it.toMeetingLinkDTO() }
    }

    fun updateMeetingLink(
        accountId: Int,
        meetingLinkId: Int,
        meetingDTO: MeetingLinkDTO
    ) {
        logger.info("updateMeetingLink called with accountId: {}, meetingLink: {} and {}", accountId, meetingLinkId, meetingDTO)
        val account = accountService.validateAndGetAccount(accountId)
        logger.info("fetched account: {}", account)
        val meetingLink = validateAndGetMeetingLink(meetingLinkId)
        logger.info("fetched meeting: {}", meetingLink)
        meetingLinkRepository.save(meetingDTO.toMeetingLink(account, meetingLinkId))
    }

    fun deleteMeetingLink(
        meetingLinkId: Int
    ) {
        logger.info("deleteMeetingLink called with: {}", meetingLinkId)
        val meetingLink = validateAndGetMeetingLink(meetingLinkId)
        logger.info("fetched meeting: {}", meetingLink)
        accountService.validateAndGetAccount(meetingLink.account.id!!).meetingLinks.remove(meetingLink)

        meetingLinkRepository.deleteById(meetingLinkId)
        if (meetingLinkRepository.findById(meetingLinkId).isPresent) {
            error("Couldn't delete meeting link with id: $meetingLinkId")
        }
    }

    private fun ensureAvailability(
        account: AccountEntity,
        meetingLinkDTO: MeetingLinkDTO
    ) = meetingLinkDTO.dates.forEach { date ->
        account.availabilities
            .filter { it.dayOfWeek == date.dayOfWeek }
            .ifEmpty { throw NotFoundException("Did not find availability for date ${date.dayOfWeek}") }
            .filter { it.startTime.minsTill(it.endTime) >= meetingLinkDTO.durationInMins }
            .ifEmpty { throw NotFoundException("Did not find available slot of size ${meetingLinkDTO.durationInMins} for date $date") }
    }

    fun validateAndGetMeetingLink(
        meetingLinkId: Int,
    ) = meetingLinkRepository
        .findById(meetingLinkId)
        .orElseThrow { NotFoundException("Meeting link with id: $meetingLinkId not found") }
}
