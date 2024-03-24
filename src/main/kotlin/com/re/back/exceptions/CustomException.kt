package com.re.back.exceptions

abstract class CustomException(val statusCode: Int, message: String, val data: Any? = null) : RuntimeException(message)