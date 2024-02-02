package com.re.back

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("Start")
class StarterController {
    @GetMapping("/secured")
    fun securedTest(): String = "Hello secured"

    @GetMapping("/authorized")
    fun notAuthorizedTest(): String = "Hello authorized"
}