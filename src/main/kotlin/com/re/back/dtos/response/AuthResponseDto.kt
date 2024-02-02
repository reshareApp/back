package com.re.back.dtos.response

import com.re.back.enums.UserRole
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
