package com.harbor.calendly.dto

import java.time.LocalDate
import java.time.LocalTime
import javax.validation.constraints.NotBlank

data class MeetingDTO(
    val meetingLinkId: Int,
    val date: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,

    @get:NotBlank(message = "name must not be blank")
    val requesterName: String,

    @get:NotBlank(message = "email must not be blank")
    val requesterEmail: String,

    @get:NotBlank(message = "phone must not be blank")
    val requesterPhone: String,

    val requesterNotes: String? = null
)
