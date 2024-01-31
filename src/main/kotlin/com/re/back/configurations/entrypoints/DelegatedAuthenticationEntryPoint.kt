package com.re.back.configurations.entrypoints

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerExceptionResolver

@Component
class DelegatedAuthenticationEntryPoint(@Qualifier("handlerExceptionResolver") private val exceptionResolver: HandlerExceptionResolver) :
    AuthenticationEntryPoint {

    companion object {
        private val LOGGER: Logger =
            LoggerFactory.getLogger(Companion::class.java)
    }

    override fun commence(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        authException: AuthenticationException?
    ) {
        LOGGER.warn("Enter the `Commence` method")
        LOGGER.warn(authException?.message ?: "NOT Specified Exception Message")
        exceptionResolver.resolveException(request!!, response!!, null, authException!!)
    }
}