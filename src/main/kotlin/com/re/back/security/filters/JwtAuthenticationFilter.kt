package com.re.back.security.filters

import com.re.back.exceptions.NotFoundAuthorizationTokenException
import com.re.back.security.jwt.JwtService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.servlet.HandlerExceptionResolver

@Component
class JwtAuthenticationFilter(
    val jwtService: JwtService,
    val userDetailsService: UserDetailsService,
    @Qualifier("handlerExceptionResolver") val exceptionResolver: HandlerExceptionResolver
) :
    OncePerRequestFilter() {

    companion object {
        private val LOGGER: Logger =
            LoggerFactory.getLogger(Companion::class.java)
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            if (isSafeRequestPath(request.servletPath)
            ) {
                LOGGER.info("Open endpoint ...")
                filterChain.doFilter(request, response)
                return
            }
            val authHeader: String? = request.getHeader("Authorization")
            if (authHeader.doesNotContainBearerToken()) {
                LOGGER.warn("Not found Authorization Header ...!!")
                throw NotFoundAuthorizationTokenException()
            }
            val jwtToken = authHeader!!.extractTokenValue()
            val username = jwtService.extractUserNameFromToken(jwtToken)
            if (SecurityContextHolder.getContext().authentication == null) {
                val foundUser = userDetailsService.loadUserByUsername(username)
                if (jwtService.isTokenValid(jwtToken, foundUser))
                    updateContext(foundUser, request)
                filterChain.doFilter(request, response)
            }
        } catch (globalException: Exception) {
            LOGGER.error(globalException.javaClass.name)
            LOGGER.error("Exception caught with type : ${globalException.message} : ${globalException.cause}")
            exceptionResolver.resolveException(request, response, null, globalException)
        }
    }

    private fun isSafeRequestPath(requestPath: String) =
        requestPath.contains("/auth/") ||
                requestPath.contains("api-docs") ||
                requestPath.contains("swagger") ||
                requestPath.contains("/images/") ||
                requestPath.contains("/Start/secured")

    private fun String?.doesNotContainBearerToken() =
        this == null || !this.startsWith("Bearer ")

    private fun String.extractTokenValue() =
        this.substringAfter("Bearer ")

    private fun updateContext(foundUser: UserDetails, request: HttpServletRequest) {
        val authToken = UsernamePasswordAuthenticationToken(foundUser, null, foundUser.authorities)
        authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
        SecurityContextHolder.getContext().authentication = authToken
    }

}