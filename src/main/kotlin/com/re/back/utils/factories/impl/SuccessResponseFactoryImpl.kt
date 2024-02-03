package com.re.back.utils.factories.impl

import com.re.back.utils.factories.base.SuccessResponseFactory
import com.re.back.utils.responses.ApiCustomResponse
import org.springframework.stereotype.Service

@Service
class SuccessResponseFactoryImpl : SuccessResponseFactory {

    override fun <T> createResponse(data: T): ApiCustomResponse {
        return ApiCustomResponse(null, 200, true, data)
    }
}