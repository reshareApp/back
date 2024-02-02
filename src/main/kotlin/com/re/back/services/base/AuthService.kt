package com.re.back.services.base

import com.re.back.dtos.request.auth.RegisterRequestDto
import com.re.back.dtos.response.auth.AuthResponseDto

interface AuthService {
    fun register(registerDto : RegisterRequestDto) : AuthResponseDto
}