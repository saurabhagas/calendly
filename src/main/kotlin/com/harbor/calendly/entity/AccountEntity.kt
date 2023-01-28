package com.harbor.calendly.entity

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.persistence.Table
import javax.persistence.UniqueConstraint

@Entity
@Table(
    name = "accounts",
    uniqueConstraints = [UniqueConstraint(columnNames=["email"]), UniqueConstraint(columnNames=["phone"])]
)
data class AccountEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    val id: Int? = null,
    val name: String,
    val email: String,
    val phone: String,
    val company: String? = null,
    val aboutMe: String? = null,
    val isActive: Boolean = true,
    @OneToMany(
        mappedBy = "account",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    val availabilities : MutableList<AvailabilityEntity> = mutableListOf(),

    @OneToMany(
        mappedBy = "account",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    val meetingLinks : MutableList<MeetingLinkEntity> = mutableListOf(),

    @OneToMany(
        mappedBy = "requesterAccount",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    val meetingsRequested : MutableList<MeetingEntity> = mutableListOf(),
)
