package com.harbor.calendly.entity

import java.time.DayOfWeek
import java.time.LocalTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "availability")
data class AvailabilityEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    val id: Int? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "id", nullable = false)
    val account: AccountEntity,
    val dayOfWeek: DayOfWeek,
    val startTime: LocalTime,
    val endTime: LocalTime
) {
    override fun toString() =
        "Availability(id=$id, accountId=${account.id}, dayOfWeek=$dayOfWeek, startTime=$startTime, endTime=$endTime)"
}