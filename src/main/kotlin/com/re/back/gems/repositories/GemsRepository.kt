package com.re.back.gems.repositories;

import com.re.back.gems.entities.Gem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GemsRepository : JpaRepository<Gem, Int>