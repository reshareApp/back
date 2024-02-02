package com.re.back.dtos.request.auth

import com.re.back.entities.auth.AppUser
import jakarta.validation.constraints.*

data class RegisterRequestDto(
    @field:Email(
        message = "Invalid email format. Please provide a valid email address."
    )
    @field:NotNull
    @field:NotBlank
    @field:NotEmpty
    val email: String,

    @field:NotNull
    @field:NotBlank
    @field:NotEmpty
    val userName: String,

    @field:NotNull
    @field:NotBlank
    @field:NotEmpty
    @field:Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
        message = "Password must be at least 8 characters long and contain at least one lowercase letter, one uppercase letter, one digit, and one special character."
    )
    val password: String
)

fun RegisterRequestDto.toAppUser(password: String) = AppUser(userName, email, password)