package com.re.back.gems.repositories;

import com.re.back.gems.entities.GemTag
import com.re.back.gems.entities.GemTagId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GemsTagsRepository : JpaRepository<GemTag, GemTagId>