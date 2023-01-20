package com.harbor.calendly.repository

import com.harbor.calendly.entity.MeetingLink
import org.springframework.data.repository.CrudRepository

interface MeetingLinkRepository : CrudRepository<MeetingLink, Int>
