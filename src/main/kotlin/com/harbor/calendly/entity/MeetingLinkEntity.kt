package com.harbor.calendly.entity

import java.time.LocalDate
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity
@Table(name = "meeting_links")
data class MeetingLinkEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    val id: Int? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "id", nullable = false)
    val account: AccountEntity,

    @OneToMany(
        mappedBy = "meetingLink",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    val meetings : MutableList<MeetingEntity> = mutableListOf(),

    val durationInMins: Int,

    @ElementCollection
    val dates: List<LocalDate>
) {
    override fun toString() = "MeetingLink(id=$id, accountId=${account.id}, durationInMins=$durationInMins, dates=$dates)"
}
