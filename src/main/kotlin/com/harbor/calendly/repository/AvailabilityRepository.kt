package com.harbor.calendly.repository

import com.harbor.calendly.entity.AvailabilityEntity
import org.springframework.data.repository.CrudRepository

interface AvailabilityRepository : CrudRepository<AvailabilityEntity, Int>
