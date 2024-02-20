package com.re.back.gems.dtos.response

import java.time.LocalDateTime

data class GemResponseDto(
    val id: Int,
    val title: String,
    val description: String? = null,
    val link: String? = null,
    val isPublic: Boolean,
    val isOriginalContent: Boolean,
    val createdOn: LocalDateTime,
    val updatedOn: LocalDateTime?,
    val isCommand: Boolean,
    val tagsNames: List<String>,
    val tagsLabels: List<String>
)
