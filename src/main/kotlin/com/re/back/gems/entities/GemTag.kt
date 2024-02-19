package com.re.back.gems.entities

import jakarta.persistence.*

@Entity
@Table(name = "gems_tags")
@IdClass(GemTagId::class)
class GemTag(@Id val gemId: Int? = null, @Id val tagId: Int? = null)