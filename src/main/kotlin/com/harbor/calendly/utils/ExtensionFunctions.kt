package com.harbor.calendly.utils

import com.harbor.calendly.dto.AccountDTO
import com.harbor.calendly.dto.AvailabilityDTO
import com.harbor.calendly.dto.MeetingLinkDTO
import com.harbor.calendly.entity.Account
import com.harbor.calendly.entity.Availability
import com.harbor.calendly.entity.MeetingLink

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
    startDate = startDate,
    endDate = endDate
)

fun MeetingLink.toMeetingLinkDTO() = MeetingLinkDTO(
    accountId = account.id!!,
    startDate = startDate,
    endDate = endDate
)
