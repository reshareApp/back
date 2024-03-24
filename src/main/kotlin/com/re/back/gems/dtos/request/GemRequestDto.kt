package com.re.back.gems.dtos.request

import jakarta.validation.constraints.*

data class GemRequestDto(
    @field:NotBlank val title: String,
    val description: String? = null,
    val link: String? = null,
    val isPublic: Boolean = false,
    val isOriginalContent: Boolean = false,
    val tags: List<String>? = null
) {
    fun formattedLink(): String = link?.trim() ?: ""

    fun isCommand(): Boolean {
        return tags?.any { t ->
            t.trim().replace(" ", "") == "command"
        } ?: false
    }
}
