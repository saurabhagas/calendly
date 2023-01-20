package com.harbor.calendly.entity

import java.time.LocalDate
import java.time.LocalTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "meetings")
data class Meeting(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    val id: Int? = null,

    @ManyToOne
    @JoinColumn(referencedColumnName = "id", nullable = false)
    val meetingLink: MeetingLink,
    val date: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val requesterName: String,
    val requesterEmail: String,
    val requesterPhone: String,
    val requesterNotes: String? = null
) {
    override fun toString() =
        "Meeting(id=$id, meetingLinkId=${meetingLink.id}, date=$date, startTime=$startTime," +
        " endTime=$endTime, requesterName='$requesterName', requesterEmail='$requesterEmail'," +
        " requesterPhone='$requesterPhone', requesterNotes='$requesterNotes')"
}
