package com.re.back.repositories

import com.re.back.entities.auth.AppUser
import com.re.back.exceptions.NotFoundCustomException
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface AppUsersRepository : JpaRepository<AppUser, Int> {

    fun findByUserName(userName: String): Optional<AppUser>
    fun existsByUserName(userName : String) : Boolean
}

// TODO : move it to separate extension file
fun <T> Optional<T>.getResult(identifier: Any? = null): T {
    if (!this.isPresent)
        throw NotFoundCustomException("Not Found Resource with Identifier : $identifier", 404)

    return this.get()
}