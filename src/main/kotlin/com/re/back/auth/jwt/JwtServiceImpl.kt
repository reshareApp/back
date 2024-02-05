package com.re.back.auth.jwt

import com.re.back.auth.dtos.jwt.JwtTokenDto
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*
import java.util.function.Function

@Service
class JwtServiceImpl(private val jwtProperties: JwtProperties) : JwtService {

    private val secretKey = Keys.hmacShaKeyFor(jwtProperties.key.toByteArray())

    override fun extractUserNameFromToken(token: String): String {
        return try {
            extractClaim<String>(token) { obj: Claims -> obj.subject }
        } catch (e: ExpiredJwtException) {
            e.claims.subject
        }
    }

    override fun generateToken(userDetails: UserDetails?): JwtTokenDto {
        return generateToken(HashMap(), userDetails)
    }

    override fun generateToken(extraClaims: Map<String, Any?>?, userDetails: UserDetails?): JwtTokenDto {
        return JwtTokenDto(buildTokenString(extraClaims, userDetails), buildExpirationDate())
    }

    private fun buildTokenString(extraClaims: Map<String, Any?>?, userDetails: UserDetails?): String {

        return Jwts.builder()
            .claims(extraClaims)
            .subject(userDetails?.username)
            .issuedAt(Date(System.currentTimeMillis()))
            .expiration(Date(System.currentTimeMillis() + jwtProperties.accessTokenExpiration))
            .signWith(secretKey)
            .compact()
    }

    private fun buildExpirationDate(): Date {
        return Date(System.currentTimeMillis() + jwtProperties.accessTokenExpiration)
    }

    override fun isTokenValid(token: String, userDetails: UserDetails): Boolean {
        val userName: String = extractUserNameFromToken(token)
        return userName == userDetails.username && !isTokenExpired(token)
    }

    override fun isTokenExpired(token: String): Boolean {
        return extractExpiration(token).before(Date())
    }

    override fun <T> extractClaim(token: String, claimsResolvers: Function<Claims, T>): T {
        val claims: Claims = extractAllClaims(token)
        return claimsResolvers.apply(claims)
    }

    override fun extractExpiration(token: String): Date {
        return extractClaim(token) { obj: Claims -> obj.expiration }
    }

    private fun extractAllClaims(token: String): Claims {
        val parser = Jwts.parser().verifyWith(secretKey).build()

        return parser
            .parseSignedClaims(token)
            .payload
    }

}