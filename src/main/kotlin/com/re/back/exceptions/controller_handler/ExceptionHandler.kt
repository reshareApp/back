package com.re.back.exceptions.controller_handler

import com.re.back.exceptions.CustomException
import com.re.back.utils.factories.base.ErrorResponseFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
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
}