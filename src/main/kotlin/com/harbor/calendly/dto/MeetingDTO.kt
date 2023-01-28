package com.harbor.calendly.dto

import java.time.LocalDate
import java.time.LocalTime

data class MeetingDTO(
    val date: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val requesterAccountId: Int,
    val requesterNotes: String? = null
)
