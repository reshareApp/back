package com.re.back.exceptions

class AlreadyUsedRegisterCredentialsException(credentialType: String) :
    CustomException(400, "Already used $credentialType ..!!")