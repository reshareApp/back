package com.re.back.dtos

import java.util.Date

data class JwtTokenDto(
    val token: String,
    val expiresOn: Date
)
