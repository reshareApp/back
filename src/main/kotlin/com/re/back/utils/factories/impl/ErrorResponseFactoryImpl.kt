package com.re.back.utils.factories.impl

import com.re.back.utils.factories.base.ErrorResponseFactory
import com.re.back.utils.responses.ApiCustomResponse
import org.springframework.stereotype.Service

@Service
class ErrorResponseFactoryImpl : ErrorResponseFactory {
    override fun createResponse(message: String, statusCode: Int): ApiCustomResponse {
        return ApiCustomResponse(message,statusCode,false,null)
    }
}