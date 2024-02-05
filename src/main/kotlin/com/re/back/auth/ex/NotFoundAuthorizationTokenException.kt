package com.re.back.auth.ex

import com.re.back.exceptions.CustomException

class NotFoundAuthorizationTokenException :
    CustomException(401, "You need to be Authorized to access this resource ...!!")