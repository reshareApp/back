package com.re.back.gems.entities

import com.re.back.auth.entities.AppUser
import com.re.back.gems.dtos.response.GemResponseDto
import jakarta.persistence.*
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.time.LocalDateTime

@Entity
@Table(name = "gems")
class Gem(
    @Column(nullable = false) val title: String,
    val description: String? = null,
    @Column val link: String? = null,
    val isPublic: Boolean = false,
    val isOriginalContent: Boolean = false,
    @Column(nullable = false) val createdOn: LocalDateTime = LocalDateTime.now(),
    @Column(nullable = true) val updatedOn: LocalDateTime? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "user_id",
        nullable = false
    ) val user: AppUser,

    @OneToMany(cascade = [CascadeType.REMOVE], mappedBy = "gem")
    @OnDelete(action = OnDeleteAction.CASCADE)
    val tags: List<GemTag>? = null,


    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Int? = null
)

fun Gem.toGemResponseDto(tagsNames: List<String> = mutableListOf(), tagsLabels: List<String> = mutableListOf()) =
    GemResponseDto(
        id!!,
        title,
        description,
        link,
        isPublic,
        isOriginalContent,
        createdOn,
        updatedOn,
        tagsNames,
        tagsLabels
    )
