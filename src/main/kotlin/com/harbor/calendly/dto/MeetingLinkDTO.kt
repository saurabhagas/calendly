package com.harbor.calendly.dto

import java.time.LocalDate

data class MeetingLinkDTO(
    val accountId: Int,
    val startDate: LocalDate,
    val endDate: LocalDate
)
