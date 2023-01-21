package com.harbor.calendly.dto

import org.hibernate.validator.constraints.Length
import java.time.LocalDate
import java.time.LocalTime
import javax.validation.constraints.Email

data class MeetingDTO(
    val meetingLinkId: Int,
    val date: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,

    @get:Length(min = 1, max = 255, message = "name must be between 1 and 255 chars length")
    val requesterName: String,

    @get:Email(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}\$", message = "email must be valid")
    val requesterEmail: String,

    @get:Length(min = 1, max = 255, message = "phone must be between 1 and 255 chars length")
    val requesterPhone: String,

    val requesterNotes: String? = null
)
