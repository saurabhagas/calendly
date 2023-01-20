package com.harbor.calendly.utils

import com.harbor.calendly.dto.AccountDTO
import com.harbor.calendly.dto.AvailabilityDTO
import com.harbor.calendly.entity.Account
import com.harbor.calendly.entity.Availability

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
