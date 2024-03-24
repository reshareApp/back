package com.re.back.gems.repositories;

import com.re.back.gems.entities.UserTag
import com.re.back.gems.entities.UserTagId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface UsersTagsRepository : JpaRepository<UserTag, UserTagId> {

    fun findByUserIdAndTagId(userId: Int, tagId: Int): Optional<UserTag>
}