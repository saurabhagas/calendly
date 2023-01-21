package com.harbor.calendly.repository

import com.harbor.calendly.entity.Availability
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import javax.transaction.Transactional

interface AvailabilityRepository : CrudRepository<Availability, Int> {
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM availability WHERE id = :id", nativeQuery = true)
    fun deleteRecord(
        @Param("id") id: Int
    ): Int
}
