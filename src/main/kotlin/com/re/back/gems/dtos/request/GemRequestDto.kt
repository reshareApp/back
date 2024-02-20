package com.re.back.gems.dtos.request

import jakarta.validation.constraints.*

data class GemRequestDto(
    @field:NotBlank val title: String,
    val description: String? = null,
    val link: String? = null,
    val isPublic: Boolean = false,
    val isOriginalContent: Boolean = false,
    @field:NotNull val isCommand: Boolean,
    val tags: List<String>? = null
)
