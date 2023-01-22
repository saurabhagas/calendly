package com.harbor.calendly.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/")
class RootController {
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun getAccount(): String = "Available endpoints: /accounts, /availability, /meeting-links, /meetings"
}
