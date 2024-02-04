package com.re.back.repositories.auth

import com.re.back.entities.auth.AppUser
import com.re.back.exceptions.NotFoundCustomException
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface AppUsersRepository : JpaRepository<AppUser, Int> {
    fun findByUserName(userName: String): Optional<AppUser>
    fun existsByUserName(userName: String): Boolean
    fun existsByEmail(email: String): Boolean
}
