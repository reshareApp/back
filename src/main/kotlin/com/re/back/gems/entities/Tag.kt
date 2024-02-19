package com.re.back.gems.entities

import jakarta.persistence.*

@Entity
@Table(name = "tags")
class Tag(
    @Column(unique = true, nullable = false) val name: String,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Int? = null
)