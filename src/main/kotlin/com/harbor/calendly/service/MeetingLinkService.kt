package com.harbor.calendly.service

import com.harbor.calendly.dto.MeetingLinkDTO
import com.harbor.calendly.exception.NotFoundException
import com.harbor.calendly.repository.AccountRepository
import com.harbor.calendly.repository.MeetingLinkRepository
import com.harbor.calendly.utils.toMeetingLink
import com.harbor.calendly.utils.toMeetingLinkDTO
import mu.KLogging
import org.springframework.stereotype.Service

@Service
class MeetingLinkService(
    val accountRepository: AccountRepository,
    val meetingLinkRepository: MeetingLinkRepository
) {
    companion object : KLogging()

    fun createMeetingLink(
        meetingLinkDTO: MeetingLinkDTO
    ): Int {
        logger.info("createMeetingLink called with: {}", meetingLinkDTO)
        require(meetingLinkDTO.endDate > meetingLinkDTO.startDate) {
            "End date should be greater than start date"
        }
        val account = validateAndGetAccount(meetingLinkDTO.accountId)
        logger.info("fetched account: {}", account)
        val result = meetingLinkRepository.save(meetingLinkDTO.toMeetingLink(account))
        logger.info("create result: {}", result)
        return requireNotNull(result.id)
    }

    fun getMeetingLink(
        meetingId: Int
    ): MeetingLinkDTO {
        logger.info("getMeeting called with: {}", meetingId)
        val meeting = validateAndGetMeetingLink(meetingId)
        logger.info("fetched meeting: {}", meeting)
        return meeting.toMeetingLinkDTO()
    }

    fun updateMeetingLink(
        meetingId: Int,
        meetingDTO: MeetingLinkDTO
    ) {
        logger.info("updateMeeting called with: {} {}", meetingId, meetingDTO)
        require(meetingDTO.endDate > meetingDTO.startDate) {
            "End date should be greater than start date"
        }
        val account = validateAndGetAccount(meetingDTO.accountId)
        logger.info("fetched account: {}", account)
        val meeting = validateAndGetMeetingLink(meetingId)
        logger.info("fetched meeting: {}", meeting)
        meetingLinkRepository.save(meetingDTO.toMeetingLink(account, meetingId))
    }

    fun deleteMeetingLink(meetingId: Int) {
        logger.info("deleteMeetingLink called with: {}", meetingId)
        val meeting = validateAndGetMeetingLink(meetingId)
        logger.info("fetched meeting: {}", meeting)
        meetingLinkRepository.deleteById(meetingId)
    }

    private fun validateAndGetMeetingLink(
        meetingId: Int,
    ) = meetingLinkRepository
        .findById(meetingId)
        .orElseThrow { NotFoundException("Meeting link with id: $meetingId not found") }

    private fun validateAndGetAccount(
        accountId: Int,
    ) = accountRepository
        .findById(accountId)
        .orElseThrow { NotFoundException("Account with id: $accountId not found") }
}
