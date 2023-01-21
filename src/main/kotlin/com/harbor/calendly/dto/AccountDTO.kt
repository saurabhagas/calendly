package com.harbor.calendly.dto

import org.hibernate.validator.constraints.Length
import javax.validation.constraints.Email

data class AccountDTO(
    @get:Length(min = 1, max = 255, message = "name must be between 1 and 255 chars length")
    val name: String,

    @get:Email(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}\$", message = "email must be valid")
    @get:Length(max = 255)
    val email: String,

    @get:Length(min = 1, max = 255, message = "phone must be between 1 and 255 chars length")
    val phone: String,
    val company: String? = null,
    val aboutMe: String? = null
)
