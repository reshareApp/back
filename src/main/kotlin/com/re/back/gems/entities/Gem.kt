package com.re.back.gems.entities

import com.re.back.auth.entities.AppUser
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "gems")
class Gem(
    @Column(nullable = false) val title: String,
    val description: String? = null,
    @Column(unique = true) val link: String? = null,
    val isPublic: Boolean = false,
    val isOriginalContent: Boolean = false,
    @Column(nullable = false) val createdOn: LocalDateTime = LocalDateTime.now(),
    @Column(nullable = true) val updatedOn: LocalDateTime? = null,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "user_id",
        nullable = false
    ) val user: AppUser,
    @Column(nullable = false) val isCommand : Boolean = false,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Int? = null
)
