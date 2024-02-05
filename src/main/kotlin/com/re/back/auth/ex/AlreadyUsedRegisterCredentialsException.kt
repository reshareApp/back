package com.re.back.auth.ex

import com.re.back.exceptions.CustomException

class AlreadyUsedRegisterCredentialsException(credentialType: String) :
    CustomException(400, "Already used $credentialType ..!!")