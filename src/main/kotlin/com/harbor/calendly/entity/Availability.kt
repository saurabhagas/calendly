package com.harbor.calendly.entity

import java.time.DayOfWeek
import java.time.LocalTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table
import javax.persistence.UniqueConstraint

@Entity
@Table(
    name = "availability",
    uniqueConstraints = [UniqueConstraint(columnNames=["account_id", "dayOfWeek"])]
)
data class Availability(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    val id: Int? = null,

    @ManyToOne
    @JoinColumn(referencedColumnName = "id", nullable = false)
    val account: Account,
    val dayOfWeek: DayOfWeek,
    val startTime: LocalTime,
    val endTime: LocalTime
) {
    override fun toString() =
        "Availability(id=$id, accountId=${account.id}, dayOfWeek=$dayOfWeek, startTime=$startTime, endTime=$endTime)"
}