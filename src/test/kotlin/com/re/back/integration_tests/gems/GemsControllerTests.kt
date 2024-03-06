package com.re.back.integration_tests.gems

import com.re.back.ApiCustomResponse
import com.re.back.auth.dtos.request.RegisterRequestDto
import com.re.back.auth.entities.AppUser
import com.re.back.auth.repositories.AppUsersRepository
import com.re.back.gems.dtos.request.GemRequestDto
import com.re.back.gems.entities.Gem
import com.re.back.gems.repositories.GemsRepository
import com.re.back.gems.repositories.TagsRepository
import com.re.back.gems.repositories.UsersTagsRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GemsControllerTests @Autowired constructor(
    val restTemplate: TestRestTemplate,
    val gemsRepository: GemsRepository,
    val usersRepository: AppUsersRepository,
    val tagsRepository: TagsRepository,
    val usersTagsRepository: UsersTagsRepository
) {
    @Value("\${api.version}")
    private var apiVersion: String? = null

    @LocalServerPort
    private var port = 0

    private lateinit var token: String

    private lateinit var uri: String

    private lateinit var user: AppUser

    private val headers: HttpHeaders = HttpHeaders()

    companion object {
        const val AUTHORIZATION_HEADER_KEY = "Authorization"
    }

    @BeforeAll
    fun setup() {
        uri = "http://localhost:$port$apiVersion/gems"

        val uri = "http://localhost:$port$apiVersion/auth/register"
        val validEmail = "omar@gmail.com"
        val validUserName = "omar"
        val validPassword = "Omar@2002"
        val validRequestBody = RegisterRequestDto(validEmail, validUserName, validPassword)
        val httpRequestEntity = HttpEntity<RegisterRequestDto>(validRequestBody)

        val responseEntity = restTemplate.postForEntity<ApiCustomResponse>(
            uri, httpRequestEntity, ApiCustomResponse::class
        )

        user = usersRepository.findByUserName(validUserName).get()
        token = (responseEntity.body?.data as LinkedHashMap<*, *>)["token"] as String
        headers.add(AUTHORIZATION_HEADER_KEY, "Bearer $token")
    }

    @BeforeEach
    fun cleanDb() {
        gemsRepository.deleteAll()
        tagsRepository.deleteAll()
        usersTagsRepository.deleteAll()
    }

    @Test
    fun `Is Token Valid`() {
        assertNotNull(token)
    }

    @Test
    fun `Add not command gem without link, return 400 BAD_REQUEST`() {
        // Arrange
        val title = "gem 1"
        val link = " "
        val requestBody = GemRequestDto(
            title = title, link = link
        )

        val httpRequestEntity = HttpEntity<GemRequestDto>(requestBody, headers)


        // Act
        val responseEntity = restTemplate.postForEntity<ApiCustomResponse>(
            uri, httpRequestEntity, ApiCustomResponse::class
        )


        // Assert
        assertEquals(responseEntity.statusCode.value(), 400)
    }


    @Test
    fun `Add not unique link, return 400 BAD_REQUEST with existed gem`() {
        // Arrange
        val title = "gem 1"
        val link = "link1"

        gemsRepository.save(Gem(title, link = link, user = user))

        val requestBody = GemRequestDto(
            title = title, link = link
        )

        val httpRequestEntity = HttpEntity<GemRequestDto>(requestBody, headers)


        // Act
        val responseEntity = restTemplate.postForEntity<ApiCustomResponse>(
            uri, httpRequestEntity, ApiCustomResponse::class
        )

        // Assert
        assertEquals(responseEntity.statusCode.value(), 400)
        assertNotNull(responseEntity.body?.data)
        assertEquals((responseEntity.body?.data as LinkedHashMap<*, *>)["link"], link)

    }

    @Test
    fun `Add gem without any tags, return 200 OK`() {
        // Arrange
        val title = "gem 1"
        val link = "link1"
        val requestBody = GemRequestDto(
            title = title, link = link,
        )

        val httpRequestEntity = HttpEntity<GemRequestDto>(requestBody, headers)


        // Act
        val responseEntity = restTemplate.postForEntity<ApiCustomResponse>(
            uri, httpRequestEntity, ApiCustomResponse::class
        )


        // Assert
        assertEquals(responseEntity.statusCode.value(), HttpStatus.OK.value())
        assertEquals((responseEntity.body?.data as LinkedHashMap<*, *>)["link"], link)
        assertEquals(tagsRepository.count(), 0)

    }

    @Test
    fun `Add gem with new tags, return 200 OK with new tags inserted`() {
        // Arrange
        val title = "gem 1"
        val link = "link"
        val tag1Name = "tag1"
        val tag2Name = "tag2"
        val tag1Label = "   tag   1"
        val tag2Label = "t  ag  2  "
        val tags = listOf(tag1Label, tag2Label)
        val requestBody = GemRequestDto(
            title = title, link = link, tags = tags
        )

        val httpRequestEntity = HttpEntity<GemRequestDto>(requestBody, headers)


        // Act
        val responseEntity = restTemplate.postForEntity<ApiCustomResponse>(
            uri, httpRequestEntity, ApiCustomResponse::class
        )


        // Assert
        assertEquals(responseEntity.statusCode.value(), HttpStatus.OK.value())
        assert(
            ((responseEntity.body?.data as LinkedHashMap<*, *>)["tagsNames"] as List<*>).containsAll(
                listOf(
                    tag1Name,
                    tag2Name
                )
            )
        )
        assert(
            ((responseEntity.body?.data as LinkedHashMap<*, *>)["tagsLabels"] as List<*>).containsAll(
                listOf(
                    tag1Label,
                    tag2Label
                )
            )
        )
        assert(tagsRepository.findAll().map { t -> t.name }.containsAll(listOf(tag1Name, tag2Name)))
        assert(usersTagsRepository.findAll().map { t -> t.label }.containsAll(listOf(tag1Label, tag2Label)))

    }

    @Test
    fun `Add gem with existed tags, return 200 OK without any insertions`() {
        // Arrange
        val title = "gem 1"
        val tag1Name = "command"
        val tag2Name = "tag2"

        val tag1Label = "comman d"
        val tag2Label = "t  ag  2  "
        val tags = listOf(tag1Label, tag2Label)
        val requestBody = GemRequestDto(
            title = title, tags = tags
        )

        val httpRequestEntity = HttpEntity<GemRequestDto>(requestBody, headers)


        // Act
        val firstResponseEntity = restTemplate.postForEntity<ApiCustomResponse>(
            uri, httpRequestEntity, ApiCustomResponse::class
        )

        val afterFirstRequestTagsCount = tagsRepository.count()
        val afterFirstRequestUserTagsCount = usersTagsRepository.count()


        val secondResponseEntity = restTemplate.postForEntity<ApiCustomResponse>(
            uri, httpRequestEntity, ApiCustomResponse::class
        )

        val afterSecondRequestTagsCount = tagsRepository.count()
        val afterSecondRequestUserTagsCount = usersTagsRepository.count()


        // Assert
        assertEquals(firstResponseEntity.statusCode.value(), HttpStatus.OK.value())
        assert(
            ((firstResponseEntity.body?.data as LinkedHashMap<*, *>)["tagsNames"] as List<*>).containsAll(
                listOf(
                    tag1Name,
                    tag2Name
                )
            )
        )
        assert(
            ((firstResponseEntity.body?.data as LinkedHashMap<*, *>)["tagsLabels"] as List<*>).containsAll(
                listOf(
                    tag1Label,
                    tag2Label
                )
            )
        )
        assert(tagsRepository.findAll().map { t -> t.name }.containsAll(listOf(tag1Name, tag2Name)))
        assert(usersTagsRepository.findAll().map { t -> t.label }.containsAll(listOf(tag1Label, tag2Label)))


        assertEquals(secondResponseEntity.statusCode.value(), HttpStatus.OK.value())
        assert(
            ((secondResponseEntity.body?.data as LinkedHashMap<*, *>)["tagsNames"] as List<*>).containsAll(
                listOf(
                    tag1Name,
                    tag2Name
                )
            )
        )
        assert(
            ((secondResponseEntity.body?.data as LinkedHashMap<*, *>)["tagsLabels"] as List<*>).containsAll(
                listOf(
                    tag1Label,
                    tag2Label
                )
            )
        )
        assert(tagsRepository.findAll().map { t -> t.name }.containsAll(listOf(tag1Name, tag2Name)))
        assert(usersTagsRepository.findAll().map { t -> t.label }.containsAll(listOf(tag1Label, tag2Label)))

        assertEquals(afterFirstRequestTagsCount, afterSecondRequestTagsCount)
        assertEquals(afterFirstRequestUserTagsCount, afterSecondRequestUserTagsCount)
    }


    fun authenticateNewToken(): String {
        val uri = "http://localhost:$port$apiVersion/auth/register"
        val validEmail = "omar2@gmail.com"
        val validUserName = "omar2"
        val validPassword = "Omar@2002"
        val validRequestBody = RegisterRequestDto(validEmail, validUserName, validPassword)
        val httpRequestEntity = HttpEntity<RegisterRequestDto>(validRequestBody)

        val responseEntity = restTemplate.postForEntity<ApiCustomResponse>(
            uri, httpRequestEntity, ApiCustomResponse::class
        )

        return (responseEntity.body?.data as LinkedHashMap<*, *>)["token"] as String
    }

    @Test
    fun `Add gem with existed tags but new labels, return 200 OK without any insertions in tags but new labels in user tags`() {
        // Arrange
        val title = "gem 1"
        val tag1Name = "command"
        val tag2Name = "tag2"

        val firstRequestTag1Label = "comma nd  "
        val firstRequestTag2Label = "tag  2"
        val firstRequestTags = listOf(firstRequestTag1Label, firstRequestTag2Label)
        val firstRequestBody = GemRequestDto(
            title = title, tags = firstRequestTags
        )

        val secondRequestTag1Label = "command"
        val secondRequestTag2Label = "t a g 2"
        val secondRequestTags = listOf(secondRequestTag1Label, secondRequestTag2Label)
        val secondRequestBody = GemRequestDto(
            title = title, tags = secondRequestTags
        )

        val firstRequestHeaders = HttpHeaders()
        firstRequestHeaders.add(AUTHORIZATION_HEADER_KEY, "Bearer $token")
        val firstHttpRequestEntity = HttpEntity<GemRequestDto>(firstRequestBody, firstRequestHeaders)

        val newToken = authenticateNewToken()
        val secondRequestHeaders = HttpHeaders()
        secondRequestHeaders.add(AUTHORIZATION_HEADER_KEY, "Bearer $newToken")
        val secondHttpRequestEntity = HttpEntity<GemRequestDto>(secondRequestBody, secondRequestHeaders)


        // Act
        val firstResponseEntity = restTemplate.postForEntity<ApiCustomResponse>(
            uri, firstHttpRequestEntity, ApiCustomResponse::class
        )

        val afterFirstRequestTagsCount = tagsRepository.count()
        val afterFirstRequestUserTagsCount = usersTagsRepository.count()


        val secondResponseEntity = restTemplate.postForEntity<ApiCustomResponse>(
            uri, secondHttpRequestEntity, ApiCustomResponse::class
        )

        val afterSecondRequestTagsCount = tagsRepository.count()
        val afterSecondRequestUserTagsCount = usersTagsRepository.count()


        // Assert
        assertEquals(firstResponseEntity.statusCode.value(), HttpStatus.OK.value())
        assert(
            ((firstResponseEntity.body?.data as LinkedHashMap<*, *>)["tagsNames"] as List<*>).containsAll(
                listOf(
                    tag1Name,
                    tag2Name
                )
            )
        )
        assert(
            ((firstResponseEntity.body?.data as LinkedHashMap<*, *>)["tagsLabels"] as List<*>).containsAll(
                listOf(
                    firstRequestTag1Label,
                    firstRequestTag2Label
                )
            )
        )
        assert(tagsRepository.findAll().map { t -> t.name }.containsAll(listOf(tag1Name, tag2Name)))
        assert(usersTagsRepository.findAll().map { t -> t.label }
            .containsAll(listOf(firstRequestTag1Label, firstRequestTag2Label)))


        assertEquals(secondResponseEntity.statusCode.value(), HttpStatus.OK.value())
        assert(
            ((secondResponseEntity.body?.data as LinkedHashMap<*, *>)["tagsNames"] as List<*>).containsAll(
                listOf(
                    tag1Name,
                    tag2Name
                )
            )
        )
        assert(
            ((secondResponseEntity.body?.data as LinkedHashMap<*, *>)["tagsLabels"] as List<*>).containsAll(
                listOf(
                    secondRequestTag1Label,
                    secondRequestTag2Label
                )
            )
        )
        assert(tagsRepository.findAll().map { t -> t.name }.containsAll(listOf(tag1Name, tag2Name)))
        assert(usersTagsRepository.findAll().map { t -> t.label }
            .containsAll(listOf(firstRequestTag1Label, firstRequestTag2Label)))

        assertEquals(afterFirstRequestTagsCount, afterSecondRequestTagsCount)
        assertEquals(afterFirstRequestUserTagsCount, 2)
        assertEquals(afterSecondRequestUserTagsCount, 4)

    }

}