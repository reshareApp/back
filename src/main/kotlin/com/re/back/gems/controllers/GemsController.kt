package com.re.back.gems.controllers

import com.re.back.extensions.buildOkApiResponseEntity
import com.re.back.gems.services.GemsService
import com.re.back.gems.dtos.request.GemRequestDto
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("\${api.version}/gems")
class GemsController(private val gemsService: GemsService) {


    @PostMapping
    fun addGem(@RequestBody @Valid gemRequestDto: GemRequestDto) =
        gemsService.addGem(gemRequestDto).buildOkApiResponseEntity()
}