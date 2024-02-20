package com.re.back.gems.services

import com.re.back.auth.entities.AppUser
import com.re.back.gems.entities.Tag
import com.re.back.gems.entities.UserTag
import com.re.back.gems.entities.UserTagId
import com.re.back.gems.repositories.TagsRepository
import com.re.back.gems.repositories.UsersTagsRepository
import org.springframework.stereotype.Service

@Service
class TagsGenerator(
    private val tagsRepository: TagsRepository,
    private val usersTagsRepository: UsersTagsRepository
) {

    private var tags = mutableListOf<Tag>()
    private var insertedNewTags = mutableListOf<Tag>()

    private var userTagsLabels = mutableListOf<UserTag>()
    private var insertedNewUserTags = mutableListOf<UserTag>()

    fun generateTagsForUser(tagsLabels: List<String>, user: AppUser): TagsResult {

        freeTagsResources()

        generateMainTags(tagsLabels)
        generateUserTagsWithLabels(user, tagsLabels)

        return TagsResult(tags, tagsLabels)
    }

    private fun freeTagsResources() {
        tags = mutableListOf()
        insertedNewTags = mutableListOf()

        userTagsLabels = mutableListOf()
        insertedNewUserTags = mutableListOf()
    }

    private fun generateMainTags(tagsLabels: List<String>) {
        tagsLabels.forEach { tagLabel ->
            val formattedTag = tagLabel.formatTag()
            val tagWithTheSameFormattedName = tagsRepository.findByName(formattedTag)

            if (tagWithTheSameFormattedName.isPresent)
                tags.add(tagWithTheSameFormattedName.get())
            else
                insertedNewTags.add(Tag(formattedTag))
        }

        tags.addAll(tagsRepository.saveAll(insertedNewTags))
    }


    private fun generateUserTagsWithLabels(user: AppUser, tagsLabels: List<String>) {
        tags.forEachIndexed { index, tag ->
            val tagId = tag.id!!

            val userTag = usersTagsRepository.findByUserIdAndTagId(user.id!!, tagId)
            if (userTag.isPresent)
                userTagsLabels.add(userTag.get())
            else {
                val tagLabel = tagsLabels[index]
                insertedNewUserTags.add(UserTag(tagLabel, user, tag, UserTagId(user.id, tagId)))
            }
        }

        userTagsLabels.addAll(usersTagsRepository.saveAll(insertedNewUserTags))
    }


}

data class TagsResult(
    val tags: List<Tag>,
    val tagsLabels: List<String>
)

fun String.formatTag() = this.lowercase().trim().replace(" ", "")
