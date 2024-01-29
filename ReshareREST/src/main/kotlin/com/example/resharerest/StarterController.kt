package com.example.resharerest

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("Start")
class StarterController {
    @GetMapping
    fun test(): String = "Hello"
}