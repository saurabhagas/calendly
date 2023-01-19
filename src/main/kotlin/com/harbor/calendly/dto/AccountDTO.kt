package com.harbor.calendly.dto

import javax.validation.constraints.NotBlank

data class AccountDTO(
    @get:NotBlank(message = "name must not be blank")
    val name: String,

    @get:NotBlank(message = "email must not be blank")
    val email: String,

    @get:NotBlank(message = "phone must not be blank")
    val phone: String,
    val company: String? = null,
    val aboutMe: String? = null
)
