package com.re.back.gems

import com.re.back.auth.entities.AppUser
import com.re.back.gems.dtos.request.GemRequestDto
import com.re.back.gems.dtos.response.GemResponseDto
import com.re.back.gems.entities.*
import com.re.back.gems.ex.AlreadyUsedLinkException
import com.re.back.gems.ex.RequiredLinkException
import com.re.back.gems.repositories.GemsRepository
import com.re.back.gems.repositories.GemsTagsRepository
import com.re.back.gems.repositories.TagsRepository
import com.re.back.gems.repositories.UsersTagsRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service


@Service
class GemsService(
    private val gemsRepository: GemsRepository,
    private val tagsRepository: TagsRepository,
    private val usersTagsRepository: UsersTagsRepository,
    private val gemsTagsRepository: GemsTagsRepository
) {
    fun addGem(gemRequestDto: GemRequestDto): GemResponseDto {
        val user = getAuthenticatedUser()
        val userId = user.id!!

        if (!gemRequestDto.isCommand && (gemRequestDto.link.isNullOrEmpty() || gemRequestDto.link.isBlank())) {
            throw RequiredLinkException()
        }

        // check if there is already gem for this user with the same link
        // ==> throw AlreadyUsedLinkException
        val formattedLink = gemRequestDto.link!!.trim()

        val gemWithSameLink = gemsRepository.findByUserIdAndLink(userId, formattedLink)
        if (gemWithSameLink.isPresent) {
            throw AlreadyUsedLinkException(gemWithSameLink.get().toGemResponseDto())
        }

        // if there is no tags ==> insert original gem without any checks for the tags
        if (gemRequestDto.tags.isNullOrEmpty()) {
            var gem = Gem(
                title = gemRequestDto.title,
                description = gemRequestDto.description,
                link = gemRequestDto.link.trim().ifEmpty { null },
                isPublic = gemRequestDto.isPublic,
                isOriginalContent = gemRequestDto.isOriginalContent,
                user = user,
                isCommand = gemRequestDto.isCommand
            )

            gem = gemsRepository.save(gem)
            return gem.toGemResponseDto()
        }


        val tags = mutableListOf<Tag>()
        val insertedNewTags = mutableListOf<Tag>()

        gemRequestDto.tags.forEach { tagLabel ->
            val formattedTag = tagLabel.formatTag()
            val tagWithTheSameFormattedName = tagsRepository.findByName(formattedTag)

            if (tagWithTheSameFormattedName.isPresent)
                tags.add(tagWithTheSameFormattedName.get())
            else
                insertedNewTags.add(Tag(formattedTag))
        }

        tags.addAll(tagsRepository.saveAll(insertedNewTags))


        val userTagsLabels = mutableListOf<UserTag>()
        val insertedNewUserTags = mutableListOf<UserTag>()

        tags.forEachIndexed { index, tag ->
            val tagId = tag.id!!

            val userTag = usersTagsRepository.findByUserIdAndTagId(userId, tagId)
            if (userTag.isPresent)
                userTagsLabels.add(userTag.get())
            else {
                val tagLabel = gemRequestDto.tags[index]
                insertedNewUserTags.add(UserTag(tagLabel, user, tag, UserTagId(userId, tagId)))
            }
        }

        userTagsLabels.addAll(usersTagsRepository.saveAll(insertedNewUserTags))

        var gem = Gem(
            title = gemRequestDto.title,
            description = gemRequestDto.description,
            link = gemRequestDto.link.trim().ifEmpty { null },
            isPublic = gemRequestDto.isPublic,
            isOriginalContent = gemRequestDto.isOriginalContent,
            user = user,
            isCommand = gemRequestDto.isCommand
        )

        gem = gemsRepository.save(gem)


        val gemTags = tags.map { tag ->
            GemTag(tag, gem,GemTagId(tag.id!!,gem.id!!))
        }
        gemsTagsRepository.saveAll(gemTags)



        return gem.toGemResponseDto(tags.map { t -> t.name }, userTagsLabels.map { ut -> ut.label })

    }

    private fun getAuthenticatedUser() = SecurityContextHolder.getContext().authentication
        .principal as AppUser

    fun String.formatTag() = this.lowercase().trim().replace(" ", "")
}