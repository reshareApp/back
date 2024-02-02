package com.re.back.utils.extensions

import com.re.back.exceptions.NotFoundCustomException
import com.re.back.utils.responses.ApiCustomResponse
import org.springframework.http.ResponseEntity
import java.time.ZoneId
import java.util.*

fun Date.toLocalDateTime() = this.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()!!

fun Any.buildOkApiResponseEntity(): ResponseEntity<ApiCustomResponse<*>> {
    val okCustomResponse = ApiCustomResponse(null, 200, true, this)
    return ResponseEntity.ok(okCustomResponse)
}

fun Any.buildErrorApiResponseEntity(message: String?, statusCode: Int = 400): ResponseEntity<ApiCustomResponse<*>> {
    val errorCustomResponse = ApiCustomResponse(message, statusCode, false, this)
    return ResponseEntity.status(statusCode).body(errorCustomResponse)
}

fun String.buildErrorApiResponseEntityFromMessage(statusCode: Int): ResponseEntity<ApiCustomResponse<*>> {
    val errorCustomResponse = ApiCustomResponse(this, statusCode, false, null)
    return ResponseEntity.status(statusCode).body(errorCustomResponse)
}

fun <T> Optional<T>.getResult(identifier: Any? = null): T {
    if (!this.isPresent)
        throw NotFoundCustomException("Not Found Resource with Identifier : $identifier")

    return this.get()
}