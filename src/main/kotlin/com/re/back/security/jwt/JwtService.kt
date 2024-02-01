package com.re.back.security.jwt

import com.re.back.dtos.JwtTokenDto
import io.jsonwebtoken.Claims
import org.springframework.security.core.userdetails.UserDetails
import java.util.*
import java.util.function.Function

interface JwtService {
    fun extractUserNameFromToken(token: String): String

    fun generateToken(userDetails: UserDetails?): JwtTokenDto

    fun isTokenValid(token: String, userDetails: UserDetails): Boolean

    fun isTokenExpired(token: String): Boolean

    fun generateToken(extraClaims: Map<String, Any?>?, userDetails: UserDetails?): JwtTokenDto

    fun <T> extractClaim(token: String, claimsResolvers: Function<Claims, T>): T

    fun extractExpiration(token: String): Date

}