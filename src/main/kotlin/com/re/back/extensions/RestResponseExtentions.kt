package com.re.back.extensions

import com.re.back.ApiCustomResponse
import com.re.back.exceptions.CustomException
import org.springframework.http.ResponseEntity

fun Any.buildOkApiResponseEntity(): ResponseEntity<ApiCustomResponse> {
    val okCustomResponse = ApiCustomResponse(null, 200, true, this)
    return ResponseEntity.ok(okCustomResponse)
}

fun Any.buildErrorApiResponseEntity(message: String?, statusCode: Int = 400): ResponseEntity<ApiCustomResponse> {
    val errorCustomResponse = ApiCustomResponse(message, statusCode, false, this)
    return ResponseEntity.status(statusCode).body(errorCustomResponse)
}

fun String.buildErrorApiResponseEntityFromMessage(statusCode: Int): ResponseEntity<ApiCustomResponse> {
    val errorCustomResponse = ApiCustomResponse(this, statusCode, false, null)
    return ResponseEntity.status(statusCode).body(errorCustomResponse)
}

fun CustomException.buildErrorApiResponseEntity(): ResponseEntity<ApiCustomResponse> {
    val errorCustomResponse = ApiCustomResponse(message, statusCode, false, data)
    return ResponseEntity.status(statusCode).body(errorCustomResponse)
}