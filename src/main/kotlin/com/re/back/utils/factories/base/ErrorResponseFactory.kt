package com.re.back.utils.factories.base

import com.re.back.utils.responses.ApiCustomResponse

interface ErrorResponseFactory : ResponseFactory {
    fun createResponse(message: String , statusCode : Int): ApiCustomResponse
}