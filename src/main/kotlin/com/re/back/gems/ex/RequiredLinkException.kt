package com.re.back.gems.ex

import com.re.back.exceptions.CustomException

class RequiredLinkException : CustomException(400, "Not Command Gem must include link !!")