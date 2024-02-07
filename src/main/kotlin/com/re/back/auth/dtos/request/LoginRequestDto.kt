package com.re.back.auth.dtos.request

import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.Length

data class LoginRequestDto(
    @field:NotBlank
    val userNameOrEmail: String,

    @field:NotBlank
    @field:Length(
        min = 8,
        message = "password must be at least 8 chars"
    )
    val password: String
)
