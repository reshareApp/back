package com.re.back.auth.services

import com.re.back.auth.dtos.jwt.JwtTokenDto
import com.re.back.auth.dtos.request.LoginRequestDto
import com.re.back.auth.dtos.request.RegisterRequestDto
import com.re.back.auth.dtos.request.toAppUser
import com.re.back.auth.dtos.response.AuthResponseDto
import com.re.back.auth.entities.AppUser
import com.re.back.auth.ex.AlreadyUsedRegisterCredentialsException
import com.re.back.auth.ex.NotMatchedPasswordException
import com.re.back.auth.jwt.JwtService
import com.re.back.auth.repositories.AppUsersRepository
import com.re.back.exceptions.NotFoundCustomException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.ZoneId
import java.util.*

@Service
class AuthService(
    private val usersRepository: AppUsersRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager
) {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(AuthService::class.java)
    }

    fun register(registerDto: RegisterRequestDto): AuthResponseDto {
        LOGGER.info("register with user ${registerDto.userName}, ${registerDto.email} ")

        if (usersRepository.existsByUserNameOrEmail(registerDto.userName, registerDto.email))
            throw AlreadyUsedRegisterCredentialsException("Email : ${registerDto.email} or Username : ${registerDto.userName}")

        val user = saveUserToDb(registerDto)

        val jwtToken = jwtService.generateToken(user)

        authenticateUser(registerDto.userName, registerDto.password)

        return toAuthResponseDto(user, jwtToken)
    }


    private fun saveUserToDb(registerDto: RegisterRequestDto): AppUser {
        val hashedPassword = passwordEncoder.encode(registerDto.password)
        var user = registerDto.toAppUser(hashedPassword)
        user = usersRepository.save(user)
        return user
    }

    private fun toAuthResponseDto(
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

    private fun authenticateUser(userName: String, password: String) {
        try {
            LOGGER.debug("Before Auth ....")
            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    userName,
                    password
                )
            )
            LOGGER.debug("After Auth ....")
        } catch (exception: AuthenticationException) {
            LOGGER.error(exception.message)
        }
    }

    fun login(loginRequestDto: LoginRequestDto): AuthResponseDto {
        LOGGER.info("login with user ${loginRequestDto.userNameOrEmail} ")

        val user =
            usersRepository.findByUserNameOrEmail(loginRequestDto.userNameOrEmail, loginRequestDto.userNameOrEmail)
                .orElseThrow {
                    NotFoundCustomException("Not found user with user name : ${loginRequestDto.userNameOrEmail}, or email : ${loginRequestDto.userNameOrEmail}. Please register first .")
                }

        if (!passwordEncoder.matches(loginRequestDto.password,user.password)){
            throw NotMatchedPasswordException()
        }

        val jwtToken = jwtService.generateToken(user)

        return toAuthResponseDto(user,jwtToken)
    }
}

fun Date.toLocalDateTime() = this.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()!!
