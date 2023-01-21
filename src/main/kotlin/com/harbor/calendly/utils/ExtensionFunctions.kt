package com.harbor.calendly.utils

import com.harbor.calendly.dto.AccountDTO
import com.harbor.calendly.dto.AvailabilityDTO
import com.harbor.calendly.dto.MeetingDTO
import com.harbor.calendly.dto.MeetingLinkDTO
import com.harbor.calendly.entity.Account
import com.harbor.calendly.entity.Availability
import com.harbor.calendly.entity.Meeting
import com.harbor.calendly.entity.MeetingLink
import java.time.LocalTime
import java.time.temporal.ChronoUnit

fun AccountDTO.toAccount(
    accountId: Int? = null
) = Account(
    id = accountId,
    name = name,
    email = email,
    phone = phone,
    company = company,
    aboutMe = aboutMe
)

fun Account.toAccountDTO() = AccountDTO(
    name = name,
    email = email,
    phone = phone,
    company = company,
    aboutMe = aboutMe
)

fun AvailabilityDTO.toAvailability(
    account: Account,
    availabilityId: Int? = null
) = Availability(
    id = availabilityId,
    account = account,
    dayOfWeek = dayOfWeek,
    startTime = startTime,
    endTime = endTime
)

fun Availability.toAvailabilityDTO() = AvailabilityDTO(
    accountId = account.id!!,
    dayOfWeek = dayOfWeek,
    startTime = startTime,
    endTime = endTime
)

fun MeetingLinkDTO.toMeetingLink(
    account: Account,
    availabilityId: Int? = null
) = MeetingLink(
    id = availabilityId,
    account = account,
    durationInMins = durationInMins,
    dates = dates
)

fun MeetingLink.toMeetingLinkDTO() = MeetingLinkDTO(
    accountId = account.id!!,
    durationInMins = durationInMins,
    dates = dates
)

fun MeetingDTO.toMeeting(
    meetingLink: MeetingLink,
    meetingId: Int?
) = Meeting(
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

fun Meeting.toMeetingDTO() = MeetingDTO(
    meetingLinkId = meetingLink.id!!,
    date = date,
    startTime = startTime,
    endTime = endTime,
    requesterName = requesterName,
    requesterEmail = requesterEmail,
    requesterPhone = requesterPhone,
    requesterNotes = requesterNotes
)

fun LocalTime.minsTill(other: LocalTime) = until(other, ChronoUnit.MINUTES).toInt()
