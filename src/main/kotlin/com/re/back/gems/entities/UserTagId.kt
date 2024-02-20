package com.re.back.gems.entities

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.io.Serializable

@Embeddable
class UserTagId(
    @Column(name = "user_id") val userId: Int? = null,
    @Column(name = "tag_id") val tagId: Int? = null
) : Serializable