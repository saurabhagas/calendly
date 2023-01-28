package com.harbor.calendly.utils

import com.harbor.calendly.dto.AvailabilityDTO
import com.harbor.calendly.entity.AccountEntity
import com.harbor.calendly.entity.AvailabilityEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.DayOfWeek
import java.time.LocalTime

class AvailabilityOverlapFinderKtTest {
    @Test
    fun testBothEmpty() {
        val result = findAllOverlaps(mutableListOf(), mutableListOf())
        assertEquals(0, result.size)
    }

    @Test
    fun testFirstEmpty() {
        val account = AccountEntity(null, "Saurabh", "saurabh@saurabh.com", "123")
        val result = findAllOverlaps(
            mutableListOf(),
            mutableListOf(AvailabilityEntity(null, account, DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(11, 0)))
        )
        assertEquals(0, result.size)
    }

    @Test
    fun testSecondEmpty() {
        val account = AccountEntity(null, "Saurabh", "saurabh@saurabh.com", "123")
        val result = findAllOverlaps(
            mutableListOf(AvailabilityEntity(null, account, DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(11, 0))),
            mutableListOf()
        )
        assertEquals(0, result.size)
    }

    @Test
    fun testNoOverlapsForAvailabilitiesInDifferentDay() {
        val account = AccountEntity(null, "Saurabh", "saurabh@saurabh.com", "123")
        val result = findAllOverlaps(
            mutableListOf(AvailabilityEntity(null, account, DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(11, 0))),
            mutableListOf(AvailabilityEntity(null, account, DayOfWeek.TUESDAY, LocalTime.of(10, 0), LocalTime.of(11, 0))),
        )
        assertEquals(0, result.size)
    }

    @Test
    fun testNoOverlapsForAvailabilitiesInSameDay() {
        val account = AccountEntity(null, "Saurabh", "saurabh@saurabh.com", "123")
        val result = findAllOverlaps(
            mutableListOf(AvailabilityEntity(null, account, DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(11, 0))),
            mutableListOf(AvailabilityEntity(null, account, DayOfWeek.MONDAY, LocalTime.of(11, 0), LocalTime.of(12, 0))),
        )
        assertEquals(0, result.size)
    }

    @Test
    fun testOverlapWhenSecondIsTheSubset() {
        val account = AccountEntity(null, "Saurabh", "saurabh@saurabh.com", "123")
        val startTime1 = LocalTime.of(10, 0)
        val endTime1 = LocalTime.of(11, 0)
        val startTime2 = LocalTime.of(10, 30)
        val result = findAllOverlaps(
            mutableListOf(AvailabilityEntity(null, account, DayOfWeek.MONDAY, startTime1, endTime1)),
            mutableListOf(AvailabilityEntity(null, account, DayOfWeek.MONDAY, startTime2, endTime1))
        )
        assertEquals(1, result.size)
        assertEquals(AvailabilityDTO(DayOfWeek.MONDAY, startTime2, endTime1), result.first())
    }

    @Test
    fun testOverlapWhenSecondContainsTheSubset() {
        val account = AccountEntity(null, "Saurabh", "saurabh@saurabh.com", "123")
        val startTime1 = LocalTime.of(10, 0)
        val endTime1 = LocalTime.of(11, 0)
        val startTime2 = LocalTime.of(10, 30)
        val endTime2 = LocalTime.of(11, 30)
        val result = findAllOverlaps(
            mutableListOf(AvailabilityEntity(null, account, DayOfWeek.MONDAY, startTime1, endTime1)),
            mutableListOf(AvailabilityEntity(null, account, DayOfWeek.MONDAY, startTime2, endTime2))
        )
        assertEquals(1, result.size)
        assertEquals(AvailabilityDTO(DayOfWeek.MONDAY, startTime2, endTime1), result.first())
    }

    @Test
    fun testOverlapWhenFirstContainsTheSupersetOfSecond() {
        val account = AccountEntity(null, "Saurabh", "saurabh@saurabh.com", "123")
        val startTime1 = LocalTime.of(10, 0)
        val endTime1 = LocalTime.of(15, 0)
        val startTime2 = LocalTime.of(11, 0)
        val endTime2 = LocalTime.of(12, 0)
        val startTime3 = LocalTime.of(13, 0)
        val endTime3 = LocalTime.of(15, 0)
        val result = findAllOverlaps(
            mutableListOf(
                AvailabilityEntity(null, account, DayOfWeek.MONDAY, startTime1, endTime1),
                AvailabilityEntity(null, account, DayOfWeek.TUESDAY, startTime1, endTime1)
            ),
            mutableListOf(
                AvailabilityEntity(null, account, DayOfWeek.MONDAY, startTime2, endTime2),
                AvailabilityEntity(null, account, DayOfWeek.MONDAY, startTime3, endTime3)
            )
        )
        assertEquals(2, result.size)
        assertEquals(AvailabilityDTO(DayOfWeek.MONDAY, startTime2, endTime2), result[0])
        assertEquals(AvailabilityDTO(DayOfWeek.MONDAY, startTime3, endTime3), result[1])
    }

    @Test
    fun testOverlapWhenSecondContainsTheSupersetOfFirst() {
        val account = AccountEntity(null, "Saurabh", "saurabh@saurabh.com", "123")
        val startTime1 = LocalTime.of(10, 0)
        val endTime1 = LocalTime.of(15, 0)
        val startTime2 = LocalTime.of(11, 0)
        val endTime2 = LocalTime.of(12, 0)
        val startTime3 = LocalTime.of(13, 0)
        val endTime3 = LocalTime.of(15, 0)
        val result = findAllOverlaps(
            mutableListOf(
                AvailabilityEntity(null, account, DayOfWeek.MONDAY, startTime2, endTime2),
                AvailabilityEntity(null, account, DayOfWeek.MONDAY, startTime3, endTime3),
            ),
            mutableListOf(
                AvailabilityEntity(null, account, DayOfWeek.MONDAY, startTime1, endTime1),
                AvailabilityEntity(null, account, DayOfWeek.TUESDAY, startTime1, endTime1)
            )
        )
        assertEquals(2, result.size)
        assertEquals(AvailabilityDTO(DayOfWeek.MONDAY, startTime2, endTime2), result[0])
        assertEquals(AvailabilityDTO(DayOfWeek.MONDAY, startTime3, endTime3), result[1])
    }

    @Test
    fun testOverlapWhenFirstContainsTheSupersetOfSecondOnMultipleDays() {
        val account = AccountEntity(null, "Saurabh", "saurabh@saurabh.com", "123")
        val startTime1 = LocalTime.of(10, 0)
        val endTime1 = LocalTime.of(15, 0)
        val startTime2 = LocalTime.of(11, 0)
        val endTime2 = LocalTime.of(12, 0)
        val startTime3 = LocalTime.of(13, 0)
        val endTime3 = LocalTime.of(15, 0)
        val result = findAllOverlaps(
            mutableListOf(
                AvailabilityEntity(null, account, DayOfWeek.MONDAY, startTime1, endTime1),
                AvailabilityEntity(null, account, DayOfWeek.TUESDAY, startTime1, endTime1)
            ),
            mutableListOf(
                AvailabilityEntity(null, account, DayOfWeek.MONDAY, startTime2, endTime2),
                AvailabilityEntity(null, account, DayOfWeek.MONDAY, startTime3, endTime3),
                AvailabilityEntity(null, account, DayOfWeek.TUESDAY, startTime2, endTime2),
                AvailabilityEntity(null, account, DayOfWeek.TUESDAY, startTime3, endTime3)
            )
        )
        assertEquals(4, result.size)
        assertEquals(AvailabilityDTO(DayOfWeek.MONDAY, startTime2, endTime2), result[0])
        assertEquals(AvailabilityDTO(DayOfWeek.MONDAY, startTime3, endTime3), result[1])
        assertEquals(AvailabilityDTO(DayOfWeek.TUESDAY, startTime2, endTime2), result[2])
        assertEquals(AvailabilityDTO(DayOfWeek.TUESDAY, startTime3, endTime3), result[3])
    }

    @Test
    fun testOverlapWithSelf() {
        val account = AccountEntity(null, "Saurabh", "saurabh@saurabh.com", "123")
        val result = findAllOverlaps(
            mutableListOf(AvailabilityEntity(null, account, DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(11, 0))),
            mutableListOf(AvailabilityEntity(null, account, DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(11, 0)))
        )
        assertEquals(1, result.size)
    }
}
