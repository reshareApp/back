package com.re.back.security.filters

import com.fasterxml.jackson.databind.ObjectMapper
import com.re.back.configurations.entrypoints.DelegatedAuthenticationEntryPoint
import com.re.back.security.jwt.JwtService
import com.re.back.utils.factories.base.ErrorResponseFactory
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.security.SignatureException
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

@Component
class JwtAuthenticationFilter(
    val jwtService: JwtService,
    val userDetailsService: UserDetailsService,
    val mapper: ObjectMapper,
    val errorResponseFactory: ErrorResponseFactory
) :
    OncePerRequestFilter() {

    companion object {
        private val LOGGER: Logger =
            LoggerFactory.getLogger(DelegatedAuthenticationEntryPoint.Companion::class.java)
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val authHeader: String = checkForAuthorizationHeader(request, response, filterChain)
                ?: return
            val jwt = authHeader.substring(7)
            val userName: String = jwtService.extractUserNameFromToken(jwt)
            if (isValidAuthenticationState(userName)) {
                LOGGER.info("=========== Before Find User Details ===========")
                val userDetails = findUserDetails(userName)
                LOGGER.info("=========== After Find User Details : ${userDetails.username}===========")
                if (isUserNotFound(response, userDetails)) return
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    LOGGER.info("Validated Token")
                    authenticateUserRequest(request, userDetails)
                    LOGGER.info("Finished Authentication User")
                }
            }
            LOGGER.info("Before go to the next filter")
            filterChain.doFilter(request, response)
            LOGGER.info("After go to the next filter")
        } catch (expiredJwtException: ExpiredJwtException) {
            handleExpiredJwtException(response)
        } catch (exception: MalformedJwtException) {
            handleWrongJwtExceptionFormatExceptions(response)
        } catch (exception: SignatureException) {
            handleWrongJwtExceptionFormatExceptions(response)
        }


    }

    @Throws(IOException::class, ServletException::class)
    private fun checkForAuthorizationHeader(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ): String? {
        val authHeader = request.getHeader("Authorization")
        if (StringUtils.isEmpty(authHeader) || !StringUtils.startsWith(authHeader, "Bearer ")
        ) {
            filterChain.doFilter(request, response)
            return null
        }
        return authHeader
    }

    private fun isValidAuthenticationState(userName: String): Boolean {
        return (userName.isNotEmpty()
                && userName.isNotBlank()
                && SecurityContextHolder.getContext().authentication == null)
    }

    private fun findUserDetails(uniquePhoneNumber: String): UserDetails {
        return userDetailsService.loadUserByUsername(uniquePhoneNumber)
    }

    @Throws(IOException::class)
    private fun isUserNotFound(response: HttpServletResponse, userDetails: UserDetails?): Boolean {
        if (userDetails == null) {
            handleNotFoundUserDetails(response)
            return true
        }
        return false
    }

    @Throws(IOException::class)
    private fun handleNotFoundUserDetails(response: HttpServletResponse) {
        response.status = HttpServletResponse.SC_NOT_FOUND
        val responseBody = errorResponseFactory.createResponse(
            "Not Found User, please register !!",
            404
        )
        val mappedResponseBody: String = mapper.writeValueAsString(responseBody)
        response.writer.write(mappedResponseBody)
    }

    private fun authenticateUserRequest(request: HttpServletRequest, userDetails: UserDetails) {
        val context = SecurityContextHolder.createEmptyContext()
        val authToken = UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.authorities
        )
        authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
        context.authentication = authToken
        SecurityContextHolder.setContext(context)
    }

    @Throws(IOException::class)
    private fun handleExpiredJwtException(response: HttpServletResponse) {
        response.status = HttpServletResponse.SC_REQUEST_TIMEOUT
        val responseBody =
            errorResponseFactory.createResponse("Your Token is Expired", HttpServletResponse.SC_REQUEST_TIMEOUT)
        val mappedResponseBody = mapper.writeValueAsString(responseBody)
        response.writer.write(mappedResponseBody)
    }

    @Throws(IOException::class)
    private fun handleWrongJwtExceptionFormatExceptions(response: HttpServletResponse) {
        response.status = HttpServletResponse.SC_BAD_REQUEST
        val responseBody =
            errorResponseFactory.createResponse("Your Token is not correct !!", HttpServletResponse.SC_BAD_REQUEST)
        val mappedResponseBody = mapper.writeValueAsString(responseBody)
        response.writer.write(mappedResponseBody)
    }
}