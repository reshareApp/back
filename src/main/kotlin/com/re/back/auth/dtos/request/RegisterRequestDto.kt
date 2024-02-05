package com.re.back.auth.dtos.request

import com.re.back.auth.entities.AppUser
import jakarta.validation.constraints.*

data class RegisterRequestDto(
    @field:Email(
        message = "Invalid email format. Please provide a valid email address."
    )
    @field:NotBlank
    val email: String,

    @field:NotBlank
    val userName: String,

    @field:NotBlank
    @field:Min(8,
            message = "password must be at least 8 chars")
    val password: String
)

fun RegisterRequestDto.toAppUser(password: String) = AppUser(userName, email, password)
