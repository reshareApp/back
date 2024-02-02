package com.re.back.exceptions

class NotFoundAuthorizationTokenException :
    CustomException(401, "You need to be Authorized to access this resource ...!!")