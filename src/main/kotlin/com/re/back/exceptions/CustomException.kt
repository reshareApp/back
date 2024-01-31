package com.re.back.exceptions

abstract class CustomException(var statusCode: Int) : RuntimeException() {
}