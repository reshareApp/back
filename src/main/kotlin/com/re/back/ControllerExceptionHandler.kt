package com.re.back

import com.re.back.exceptions.CustomException
import com.re.back.extensions.buildErrorApiResponseEntity
import com.re.back.extensions.buildErrorApiResponseEntityFromMessage
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.security.SignatureException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.AuthenticationException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice


@RestControllerAdvice
class ControllerExceptionHandler {

    @ExceptionHandler
    fun handleExceptions(globalException: Exception): ResponseEntity<ApiCustomResponse> {
        return when (globalException) {
            is CustomException -> {
                globalException.buildErrorApiResponseEntity()
            }

            is MethodArgumentNotValidException -> {
                val errors = HashMap<String, String?>()
                globalException.bindingResult.allErrors.forEach { error ->
                    val fieldName = (error as FieldError).field
                    val errorMessage = error.getDefaultMessage()
                    errors[fieldName] = errorMessage
                }
                errors.buildErrorApiResponseEntity("Please validate your inputs .. !!")

            }

            is AuthenticationException -> {
                val message =
                    "You are UN_AUTHORIZED of accessing this resource with exception : ${globalException.message}"
                message.buildErrorApiResponseEntityFromMessage(HttpStatus.UNAUTHORIZED.value())

            }

            is AccessDeniedException -> {
                val message = "Forbidden Authorization Exception"
                message.buildErrorApiResponseEntityFromMessage(HttpStatus.FORBIDDEN.value())

            }

            is ExpiredJwtException -> {
                "JWT Token is already expired ...!!".buildErrorApiResponseEntityFromMessage(HttpStatus.FORBIDDEN.value())
            }

            is SignatureException -> {
                (globalException.message
                    ?: "JWT Token has not correct format ...!!")
                    .buildErrorApiResponseEntityFromMessage(HttpStatus.FORBIDDEN.value())
            }

            is MalformedJwtException -> {
                (globalException.message
                    ?: "JWT Token has not correct format ...!!")
                    .buildErrorApiResponseEntityFromMessage(HttpStatus.FORBIDDEN.value())

            }

            else -> {
                "SERVER_ERROR_EXCEPTION , with cause : ${globalException.message}".buildErrorApiResponseEntityFromMessage(
                    HttpStatus.INTERNAL_SERVER_ERROR.value()
                )
            }
        }
    }
}