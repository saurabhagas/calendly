package com.harbor.calendly.repository

import com.harbor.calendly.entity.Meeting
import org.springframework.data.repository.CrudRepository

interface MeetingRepository : CrudRepository<Meeting, Int>
