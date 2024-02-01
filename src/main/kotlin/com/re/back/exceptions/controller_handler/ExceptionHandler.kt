package com.re.back.exceptions.controller_handler

import com.re.back.exceptions.CustomException
import com.re.back.utils.factories.base.ErrorResponseFactory
import com.re.back.utils.responses.ApiCustomResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
@ResponseBody
class ExceptionHandler(private val errorResponseFactory: ErrorResponseFactory) {


    @ExceptionHandler(CustomException::class)
    fun handleCustomException(exception: CustomException): ResponseEntity<*> {
        val response = errorResponseFactory.createResponse(exception.message!!, exception.statusCode)

        return ResponseEntity
            .status(response.statusCode)
            .body(response)
    }

    @ExceptionHandler(org.springframework.security.core.AuthenticationException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleGlobalUnAuthenticatedException(
        e: org.springframework.security.core.AuthenticationException?
    ): ResponseEntity<ApiCustomResponse<*>> {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value())
            .body(
                errorResponseFactory.createResponse(
                    "You are UN_AUTHORIZED of accessing this resource with exception : ${e?.message}",
                    401
                )
            )
    }
}