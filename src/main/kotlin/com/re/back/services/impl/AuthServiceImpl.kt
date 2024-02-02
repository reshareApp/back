package com.re.back.services.impl

import com.re.back.dtos.jwt.JwtTokenDto
import com.re.back.dtos.request.auth.RegisterRequestDto
import com.re.back.dtos.request.auth.toAppUser
import com.re.back.dtos.response.AuthResponseDto
import com.re.back.entities.auth.AppUser
import com.re.back.exceptions.AlreadyUsedRegisterCredentialsException
import com.re.back.repositories.AppUsersRepository
import com.re.back.security.jwt.JwtService
import com.re.back.services.base.AuthService
import com.re.back.utils.extensions.toLocalDateTime
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthServiceImpl(
    private val usersRepository: AppUsersRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager
) : AuthService {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(AuthServiceImpl::class.java)
    }

    override fun register(registerDto: RegisterRequestDto): AuthResponseDto {

        checkUniquenessOfUserCredentials(registerDto)

        val user = saveUserToDb(registerDto)

        val jwtToken = generateJwtTokenFromUserCredentials(user)

        authenticateUser(registerDto.userName, passwordEncoder.encode(registerDto.password))

        return buildAuthResponseDto(user, jwtToken)
    }

    private fun generateJwtTokenFromUserCredentials(user: AppUser) = jwtService.generateToken(user)

    private fun saveUserToDb(registerDto: RegisterRequestDto): AppUser {
        val hashedPassword = passwordEncoder.encode(registerDto.password)
        var user = registerDto.toAppUser(hashedPassword)
        user = usersRepository.save(user)
        return user
    }

    private fun buildAuthResponseDto(
        user: AppUser,
        jwtToken: JwtTokenDto
    ): AuthResponseDto {
        return AuthResponseDto(
            user.id,
            user.userName,
            user.email,
            user.role,
            user.bio,
            jwtToken.token,
            jwtToken.expiresOn.toLocalDateTime()
        )
    }

    private fun checkUniquenessOfUserCredentials(registerDto: RegisterRequestDto) {

        if (usersRepository.existsByEmail(registerDto.email))
            throw AlreadyUsedRegisterCredentialsException("Email : ${registerDto.email}")

        if (usersRepository.existsByUserName(registerDto.userName))
            throw AlreadyUsedRegisterCredentialsException("User Name : ${registerDto.userName}")

    }

    private fun authenticateUser(userName: String, password: String) {
        try {
            LOGGER.info("Before Auth ....")
            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    userName,
                    password
                )
            )
            LOGGER.info("After Auth ....")
        } catch (exception: AuthenticationException) {
            LOGGER.warn(exception.message)
        }
    }
}