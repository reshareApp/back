package com.re.back.dtos.jwt

import java.util.Date

data class JwtTokenDto(
    val token: String,
    val expiresOn: Date
)
