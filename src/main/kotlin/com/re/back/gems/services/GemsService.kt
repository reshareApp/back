package com.re.back.gems.services

import com.re.back.auth.entities.AppUser
import com.re.back.gems.dtos.request.GemRequestDto
import com.re.back.gems.dtos.response.GemResponseDto
import com.re.back.gems.entities.*
import com.re.back.gems.ex.AlreadyUsedLinkException
import com.re.back.gems.ex.RequiredLinkException
import com.re.back.gems.repositories.GemsRepository
import com.re.back.gems.repositories.GemsTagsRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service


@Service
class GemsService(
    private val tagsGenerator: TagsGenerator,
    private val gemsRepository: GemsRepository,
    private val gemsTagsRepository: GemsTagsRepository
) {
    fun addGem(gemRequestDto: GemRequestDto): GemResponseDto {

        val user = getAuthenticatedUser()

        checkForGemRequestDto(gemRequestDto)

        checkForLinkUniquenessForTheSameUser(user, gemRequestDto.link)

        if (gemRequestDto.tags.isNullOrEmpty()) {
            val gem = saveGem(gemRequestDto, user)
            return gem.toGemResponseDto()
        }

        val tagsResult = tagsGenerator.generateTagsForUser(tagsLabels = gemRequestDto.tags, user = user)

        val gem = saveGem(gemRequestDto, user)

        generateGemsWithTags(tagsResult.tags, gem)


        return gem.toGemResponseDto(tagsResult.tags.map { t -> t.name }, tagsResult.tagsLabels)
    }

    private fun generateGemsWithTags(tags: List<Tag>, gem: Gem) {
        val gemTags = tags.map { tag ->
            GemTag(tag, gem, GemTagId(tag.id!!, gem.id!!))
        }
        gemsTagsRepository.saveAll(gemTags)
    }

    private fun saveGem(
        gemRequestDto: GemRequestDto,
        user: AppUser
    ): Gem {
        var gem = Gem(
            title = gemRequestDto.title,
            description = gemRequestDto.description,
            link = gemRequestDto.link?.trim()?.ifEmpty { null },
            isPublic = gemRequestDto.isPublic,
            isOriginalContent = gemRequestDto.isOriginalContent,
            user = user,
        )

        gem = gemsRepository.save(gem)
        return gem
    }

    private fun checkForLinkUniquenessForTheSameUser(user: AppUser, link: String?) {
        when {
            link != null -> {
                val gemWithSameLink = gemsRepository.findByUserIdAndLink(user.id!!, link)
                if (gemWithSameLink.isPresent) {
                    throw AlreadyUsedLinkException(gemWithSameLink.get().toGemResponseDto())
                }
            }
        }
    }

    private fun checkForGemRequestDto(gemRequestDto: GemRequestDto) {
        if (!gemRequestDto.isCommand() && (gemRequestDto.link.isNullOrEmpty() || gemRequestDto.link.isBlank())) {
            throw RequiredLinkException()
        }
    }

    private fun getAuthenticatedUser() = SecurityContextHolder.getContext().authentication
        .principal as AppUser

}