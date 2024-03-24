package com.re.back.gems.ex

import com.re.back.exceptions.CustomException
import com.re.back.gems.dtos.response.GemResponseDto

class AlreadyUsedLinkException(gem: GemResponseDto) : CustomException(400, "There is already Gem includes this link !!", gem)