package com.re.back.gems.entities

import jakarta.persistence.*
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction

@Entity
@Table(name = "tags")
class Tag(
    @Column(unique = true, nullable = false) val name: String,

    @OneToMany(cascade = [CascadeType.REMOVE], mappedBy = "tag")
    @OnDelete(action = OnDeleteAction.CASCADE)
    val gems: List<GemTag>? = null,

    @OneToMany(cascade = [CascadeType.REMOVE], mappedBy = "tag")
    @OnDelete(action = OnDeleteAction.CASCADE)
    val users: List<UserTag>? = null,

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Int? = null
)