package com.re.back.exceptions

class NotFoundCustomException(message: String?, statusCode: Int)
    : CustomException(statusCode) {
}