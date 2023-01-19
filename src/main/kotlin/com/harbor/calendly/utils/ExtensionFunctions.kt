package com.harbor.calendly.utils

import com.harbor.calendly.dto.AccountDTO
import com.harbor.calendly.entity.Account

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
