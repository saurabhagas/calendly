package com.harbor.calendly.repository

import com.harbor.calendly.entity.MeetingEntity
import org.springframework.data.repository.CrudRepository

interface MeetingRepository : CrudRepository<MeetingEntity, Int>
