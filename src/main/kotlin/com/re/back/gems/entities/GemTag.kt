package com.re.back.gems.entities

import jakarta.persistence.*

@Entity
@Table(name = "gems_tags")
class GemTag(

    @ManyToOne
    @MapsId("tagId")
    @JoinColumn(name = "tag_id")
    val tag: Tag,

    @ManyToOne
    @MapsId("gemId")
    @JoinColumn(name = "gem_id")
    val gem: Gem,

    @EmbeddedId
    val id: GemTagId? = null
)