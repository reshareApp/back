package com.re.back.utils.responses

data class ApiCustomResponse<T>(
    val message: String?,
    val statusCode: Int,
    val isSuccess: Boolean,
    val date: T?
)
