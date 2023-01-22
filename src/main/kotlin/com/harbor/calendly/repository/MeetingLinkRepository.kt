package com.harbor.calendly.repository

import com.harbor.calendly.entity.MeetingLinkEntity
import org.springframework.data.repository.CrudRepository

interface MeetingLinkRepository : CrudRepository<MeetingLinkEntity, Int>
