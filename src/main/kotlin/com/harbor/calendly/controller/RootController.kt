package com.harbor.calendly.controller

import com.google.gson.GsonBuilder
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/")
class RootController {
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    fun getAccount() = GsonBuilder().setPrettyPrinting().create().toJson(Endpoints.values)
}

class Endpoints {
    enum class Endpoint(val description: String, val url: String) {
        ACCOUNT_CRUD("Create/Get/Update/Deactivate account", ACCOUNT_URL),
        AVAILABILITY_CRUD("Create/Get/Update/Delete availability", AVAILABILITY_URL),
        MEETING_LINK_CRUD("Create/Get/Update/Delete meeting links for host", MEETING_LINKS_URL),
        FIND_OVERLAPS("Get overlaps in availabilities of host and requester", OVERLAP_URL),
        MEETING_CRUD("Create/Get/Update/Delete meetings with host", MEETINGS_URL)
    }

    data class Pair(
        val description: String,
        val url: String
    )

    companion object {
        const val ACCOUNT_URL = "/accounts"
        const val AVAILABILITY_URL = "/accounts/{accountId}/availability"
        const val MEETING_LINKS_URL = "/accounts/{accountId}/meeting-links"
        const val OVERLAP_URL = "/accounts/{accountId}/overlap"
        const val MEETINGS_URL = "/accounts/{accountId}/meeting-links/{meetingLinkId}/meetings"
        val values = Endpoint.values().map { Pair(it.description, it.url) }
    }
}

