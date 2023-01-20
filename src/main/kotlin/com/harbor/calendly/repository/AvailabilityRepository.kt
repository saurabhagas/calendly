package com.harbor.calendly.repository

import com.harbor.calendly.entity.Availability
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.transaction.annotation.Transactional

interface AvailabilityRepository : CrudRepository<Availability, Int> {
    @Query(value = "SELECT * FROM availability WHERE account_id = :accountId AND day_of_week = :dayOfWeek", nativeQuery = true)
    fun findByAccountAndDayOfWeek(
        @Param("accountId") accountId: Int,
        @Param("dayOfWeek") dayOfWeek: Int
    ): Availability?

    @Modifying
    @Transactional
    @Query(value = "UPDATE availability SET start_time = :startTime, end_time = :endTime WHERE account_id = :accountId AND day_of_week = :dayOfWeek", nativeQuery = true)
    fun updateAvailability(
        @Param("startTime") startTime: Int,
        @Param("endTime") endTime: Int,
        @Param("accountId") accountId: Int,
        @Param("dayOfWeek") dayOfWeek: Int
    ): Int
}
