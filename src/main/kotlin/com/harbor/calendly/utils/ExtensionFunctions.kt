package com.harbor.calendly.utils

import com.harbor.calendly.dto.AccountDTO
import com.harbor.calendly.dto.AvailabilityDTO
import com.harbor.calendly.dto.MeetingDTO
import com.harbor.calendly.dto.MeetingLinkDTO
import com.harbor.calendly.entity.AccountEntity
import com.harbor.calendly.entity.AvailabilityEntity
import com.harbor.calendly.entity.MeetingEntity
import com.harbor.calendly.entity.MeetingLinkEntity
import java.time.LocalTime
import java.time.temporal.ChronoUnit

fun AccountDTO.toAccount(
    accountId: Int? = null
) = AccountEntity(
    id = accountId,
    name = name,
    email = email,
    phone = phone,
    company = company,
    aboutMe = aboutMe
)

fun AccountEntity.toAccountDTO() = AccountDTO(
    name = name,
    email = email,
    phone = phone,
    company = company,
    aboutMe = aboutMe
)

fun AvailabilityDTO.toAvailability(
    account: AccountEntity,
    availabilityId: Int? = null
) = AvailabilityEntity(
    id = availabilityId,
    account = account,
    dayOfWeek = dayOfWeek,
    startTime = startTime,
    endTime = endTime
)

fun AvailabilityEntity.toAvailabilityDTO() = AvailabilityDTO(
    dayOfWeek = dayOfWeek,
    startTime = startTime,
    endTime = endTime
)

fun MeetingLinkDTO.toMeetingLink(
    account: AccountEntity,
    availabilityId: Int? = null
) = MeetingLinkEntity(
    id = availabilityId,
    account = account,
    durationInMins = durationInMins,
    dates = dates
)

fun MeetingLinkEntity.toMeetingLinkDTO() = MeetingLinkDTO(
    durationInMins = durationInMins,
    dates = dates
)

fun MeetingDTO.toMeeting(
    meetingLink: MeetingLinkEntity,
    meetingId: Int?
) = MeetingEntity(
    id = meetingId,
    meetingLink = meetingLink,
    date = date,
    startTime = startTime,
    endTime = endTime,
    requesterName = requesterName,
    requesterEmail = requesterEmail,
    requesterPhone = requesterPhone,
    requesterNotes = requesterNotes
)

fun MeetingEntity.toMeetingDTO() = MeetingDTO(
    date = date,
    startTime = startTime,
    endTime = endTime,
    requesterName = requesterName,
    requesterEmail = requesterEmail,
    requesterPhone = requesterPhone,
    requesterNotes = requesterNotes
)

fun LocalTime.minsTill(other: LocalTime) = until(other, ChronoUnit.MINUTES).toInt()
