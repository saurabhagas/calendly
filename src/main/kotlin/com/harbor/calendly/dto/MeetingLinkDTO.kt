package com.harbor.calendly.dto

import java.time.LocalDate
import javax.validation.constraints.DecimalMin
import javax.validation.constraints.Size

data class MeetingLinkDTO(
    val accountId: Int,
    @get:DecimalMin(value = "15", message = "Can't create a meeting link < 15mins in length")
    val durationInMins: Int,

    @get:Size(min = 1, max = 30)
    val dates: List<LocalDate>
)
