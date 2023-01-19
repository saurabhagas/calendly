package com.harbor.calendly.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table
import javax.persistence.UniqueConstraint

@Entity
@Table(name = "accounts",  uniqueConstraints = [UniqueConstraint(columnNames=["email", "phone"])])
data class Account(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    val id: Int? = null,
    val name: String,
    val email: String,
    val phone: String,
    val company: String? = null,
    val aboutMe: String? = null,
    val isActive: Boolean = true
)
