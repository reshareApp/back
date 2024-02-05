package com.re.back.auth.dtos.response

import com.re.back.auth.enums.UserRole
import java.time.LocalDateTime

data class AuthResponseDto(
        val id : Int?,
        val userName : String,
        val email : String,
        val role : UserRole,
        val bio : String?,
        val token : String,
        val expiresOn : LocalDateTime
)
