package com.harbor.calendly.dto

import java.time.DayOfWeek
import java.time.LocalTime

data class AvailabilityDTO(
    val dayOfWeek: DayOfWeek,
    val startTime: LocalTime,
    val endTime: LocalTime
)
