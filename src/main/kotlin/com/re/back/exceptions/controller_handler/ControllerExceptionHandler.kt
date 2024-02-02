package com.re.back.exceptions.controller_handler

import com.re.back.exceptions.CustomException
import com.re.back.utils.extensions.buildErrorApiResponseEntity
import com.re.back.utils.extensions.buildErrorApiResponseEntityFromMessage
import com.re.back.utils.responses.ApiCustomResponse
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.security.SignatureException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.AuthenticationException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice


@RestControllerAdvice
class ControllerExceptionHandler {


    @ExceptionHandler
    fun handleExceptions(globalException: Exception): ResponseEntity<ApiCustomResponse<*>> {
        return when (globalException) {
            is CustomException -> {
                globalException.message!!.buildErrorApiResponseEntityFromMessage(globalException.statusCode)
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

    // TODO : if the new way of exception handling better than multi methods with single exception type, please delete all commented old methods
    /*
        @ExceptionHandler(CustomException::class)
        fun handleCustomException(exception: CustomException): ResponseEntity<*> {
            return exception.message!!.buildErrorApiResponseEntityFromMessage(exception.statusCode)
        }

        @ResponseStatus(HttpStatus.BAD_REQUEST)
        @ExceptionHandler(MethodArgumentNotValidException::class)
        fun handleValidationExceptions(
            ex: MethodArgumentNotValidException
        ): ResponseEntity<ApiCustomResponse<*>> {
            val errors = HashMap<String, String?>()
            ex.bindingResult.allErrors.forEach { error ->
                val fieldName = (error as FieldError).field
                val errorMessage = error.getDefaultMessage()
                errors[fieldName] = errorMessage
            }
            return errors.buildErrorApiResponseEntity("Please validate your inputs .. !!")
        }

        @ExceptionHandler(AuthenticationException::class)
        @ResponseStatus(HttpStatus.UNAUTHORIZED)
        fun handleGlobalUnAuthenticatedException(
            e: AuthenticationException?
        ): ResponseEntity<ApiCustomResponse<*>> {
            val message = "You are UN_AUTHORIZED of accessing this resource with exception : ${e?.message}"
            return message.buildErrorApiResponseEntityFromMessage(401)
        }

        @ExceptionHandler(AccessDeniedException::class)
        @ResponseStatus(HttpStatus.FORBIDDEN)
        fun handleGlobalUnAuthenticatedException(
            e: AccessDeniedException?
        ): ResponseEntity<ApiCustomResponse<*>> {
            val message = "Forbidden Authorization Exception"
            return message.buildErrorApiResponseEntityFromMessage(HttpStatus.FORBIDDEN.value())
        }

        @ExceptionHandler(ExpiredJwtException::class)
        @ResponseStatus(HttpStatus.FORBIDDEN)
        fun handleExpiredJwtException(e: ExpiredJwtException): ResponseEntity<ApiCustomResponse<*>> {
            return "JWT Token is already expired ...!!".buildErrorApiResponseEntityFromMessage(HttpStatus.FORBIDDEN.value())
        }

        @ExceptionHandler(SignatureException::class)
        @ResponseStatus(HttpStatus.FORBIDDEN)
        fun handleSignatureException(e: SignatureException): ResponseEntity<ApiCustomResponse<*>> {
            return (e.message
                ?: "JWT Token has not correct format ...!!")
                .buildErrorApiResponseEntityFromMessage(HttpStatus.FORBIDDEN.value())
        }

        @ExceptionHandler(MalformedJwtException::class)
        @ResponseStatus(HttpStatus.FORBIDDEN)
        fun handleMalformedJwtException(e: MalformedJwtException): ResponseEntity<ApiCustomResponse<*>> {
            return (e.message
                ?: "JWT Token has not correct format ...!!")
                .buildErrorApiResponseEntityFromMessage(HttpStatus.FORBIDDEN.value())
        }

     */
}