package com.re.back.auth.ex

import com.re.back.exceptions.CustomException

class NotMatchedPasswordException : CustomException(400, "Not correct password, please try again ..!!")