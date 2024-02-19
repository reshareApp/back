package com.re.back.gems.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import jakarta.persistence.Table

@Entity
@Table(name = "users_tags")
@IdClass(UserTagId::class)
class UserTag(
    @Column(nullable = false) val label: String,
    @Id val userId: Int? = null,
    @Id val tagId: Int? = null
)