package com.re.back.exceptions

abstract class CustomException(val statusCode: Int, message: String) : RuntimeException(message)