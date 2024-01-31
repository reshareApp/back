package com.re.back.utils.factories.base

import com.re.back.utils.responses.ApiCustomResponse

interface SuccessResponseFactory : ResponseFactory {
    fun <T> createResponse(data: T): ApiCustomResponse<T>
}