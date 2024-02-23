package com.re.back.gems.entities

import com.re.back.auth.entities.AppUser
import jakarta.persistence.*

@Entity
@Table(name = "users_tags")
class UserTag(
    @Column(nullable = false) val label: String,

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    val user : AppUser,

    @ManyToOne
    @MapsId("tagId")
    @JoinColumn(name = "tag_id")
    val tag : Tag,


    @EmbeddedId
    val id: UserTagId? = null
){
    override fun toString(): String {
        return "User : ${id?.userId} , Tag : ${id?.tagId} , Label : $label"
    }
}