package com.re.back.exceptions.controller_handler

import com.re.back.exceptions.CustomException
import com.re.back.utils.extensions.buildErrorApiResponseEntity
import com.re.back.utils.extensions.buildErrorApiResponseEntityFromMessage
import com.re.back.utils.responses.ApiCustomResponse
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
@ResponseBody
class ControllerExceptionHandler {


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
}