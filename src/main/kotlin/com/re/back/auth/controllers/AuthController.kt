package com.re.back.auth.controllers

import com.re.back.auth.dtos.request.RegisterRequestDto
import com.re.back.auth.services.AuthService
import com.re.back.extensions.buildOkApiResponseEntity
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("\${api.version}/auth")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/register")
    fun register(@Valid @RequestBody registerRequestDto: RegisterRequestDto) =
        authService.register(registerRequestDto).buildOkApiResponseEntity()
}