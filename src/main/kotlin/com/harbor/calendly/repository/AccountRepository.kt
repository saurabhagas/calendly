package com.harbor.calendly.repository

import com.harbor.calendly.entity.Account
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.transaction.annotation.Transactional

interface AccountRepository : CrudRepository<Account, Int> {
    @Modifying
    @Transactional
    @Query(value = "UPDATE accounts SET is_active = false WHERE id = :id", nativeQuery = true)
    fun deactivateAccount(@Param("id") id : Int) : Int

    fun countByEmail(email : String) : Int

    fun countByPhone(phone : String) : Int
}