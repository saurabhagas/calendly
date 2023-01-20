package com.harbor.calendly.repository

import com.harbor.calendly.entity.Availability
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

interface AvailabilityRepository : CrudRepository<Availability, Int> {
    @Query(value = "SELECT * from availability WHERE account_id = :accountId AND day_of_week = :dayOfWeek", nativeQuery = true)
    fun findByAccountAndDayOfWeek(
        @Param("accountId") accountId: Int,
        @Param("dayOfWeek") dayOfWeek: Int
    ): Availability?
}
