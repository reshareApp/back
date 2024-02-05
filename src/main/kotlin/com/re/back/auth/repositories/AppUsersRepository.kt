package com.re.back.auth.repositories

import com.re.back.auth.entities.AppUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface AppUsersRepository : JpaRepository<AppUser, Int> {
    fun findByUserName(userName: String): Optional<AppUser>
    fun existsByUserNameOrEmail(userName: String, email: String): Boolean
}
