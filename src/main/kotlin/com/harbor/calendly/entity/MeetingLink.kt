package com.harbor.calendly.entity

import java.time.LocalDate
import javax.persistence.Column
import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "meeting_links")
data class MeetingLink(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    val id: Int? = null,

    @ManyToOne
    @JoinColumn(referencedColumnName = "id", nullable = false)
    val account: Account,
    val durationInMins: Int,

    @ElementCollection
    val dates: List<LocalDate>
) {
    override fun toString() = "MeetingLink(id=$id, accountId=${account.id}, durationInMins=$durationInMins, dates=$dates)"
}
