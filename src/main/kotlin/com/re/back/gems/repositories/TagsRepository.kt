package com.re.back.gems.repositories;

import com.re.back.gems.entities.Tag
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TagsRepository : JpaRepository<Tag, Int>