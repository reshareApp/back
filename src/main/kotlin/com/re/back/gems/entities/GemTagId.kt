package com.re.back.gems.entities

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.io.Serializable

@Embeddable
class GemTagId(
    @Column(name = "tag_id")
    val tagId: Int,

    @Column(name = "gem_id")
    val gemId: Int
) : Serializable