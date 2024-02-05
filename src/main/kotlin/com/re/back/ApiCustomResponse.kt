package com.re.back

data class ApiCustomResponse(
    val message: String?,
    val statusCode: Int,
    val isSuccess: Boolean,
    val data: Any?
)
