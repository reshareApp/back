package com.re.back.integration_tests.auth


import com.re.back.dtos.request.auth.RegisterRequestDto
import com.re.back.entities.auth.AppUser
import com.re.back.enums.UserRole
import com.re.back.repositories.auth.AppUsersRepository
import com.re.back.utils.responses.ApiCustomResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerTests @Autowired constructor(
    val restTemplate: TestRestTemplate,
    val usersRepository: AppUsersRepository
) {

    @Value("\${api.version}")
    private var apiVersion: String? = null

    @LocalServerPort
    var port = 0

    @BeforeEach
    fun setup() {
        usersRepository.deleteAll()
    }

    @Test
    fun `Register with valid inputs, returns 200 OK`() {
        // Arrange
        val uri = "http://localhost:$port$apiVersion/auth/register"
        val validEmail = "omar@gmail.com"
        val validUserName = "omar"
        val validPassword = "Omar@2002"
        val validRequestBody = RegisterRequestDto(validEmail, validUserName, validPassword)
        val httpRequestEntity = HttpEntity<RegisterRequestDto>(validRequestBody)

        // Act
        val responseEntity = restTemplate.postForEntity<ApiCustomResponse>(
            uri,
            httpRequestEntity,
            ApiCustomResponse::class
        )


        // Assert
        assertEquals(responseEntity.statusCode.value(), HttpStatus.OK.value())
        assertEquals(responseEntity.body?.isSuccess, true)
        assertEquals((responseEntity.body?.data as LinkedHashMap<*, *>)["email"], validEmail)
        assertEquals((responseEntity.body?.data as LinkedHashMap<*, *>)["userName"], validUserName)

    }

    @Test
    fun `Register with not valid input, returns 400 BAD_REQUEST`() {
        // Arrange
        val uri = "http://localhost:$port$apiVersion/auth/register"
        val notValidEmail = "omar"
        val notValidUserName = "omar"
        val notValidPassword = "omar2002"
        val notValidRequestBody = RegisterRequestDto(notValidEmail, notValidUserName, notValidPassword)
        val httpRequestEntity = HttpEntity<RegisterRequestDto>(notValidRequestBody)

        // Act
        val responseEntity = restTemplate.postForEntity<ApiCustomResponse>(
            uri,
            httpRequestEntity,
            ApiCustomResponse::class
        )

        // Assert
        assertEquals(responseEntity.statusCode.value(), HttpStatus.BAD_REQUEST.value())
        assertEquals(responseEntity.body?.isSuccess, false)
        assertNotNull(responseEntity.body?.message)
    }


    @Test
    fun `Register with taken username Or email, returns 400 BAD_REQUEST`() {
        // Arrange
        val uri = "http://localhost:$port$apiVersion/auth/register"
        val validEmail = "omar@gmail.com"
        val validUserName = "omar"
        val validPassword = "Omar@2002"
        val validRequestBody = RegisterRequestDto(validEmail, validUserName, validPassword)
        val httpRequestEntity = HttpEntity<RegisterRequestDto>(validRequestBody)

        usersRepository.save(
            AppUser(
                validUserName,
                validEmail,
                "FAKE_PASSWORD",
                null,
                UserRole.ROLE_USER
            ))


        // Act
        val responseEntity = restTemplate.postForEntity<ApiCustomResponse>(
            uri,
            httpRequestEntity,
            ApiCustomResponse::class
        )


        // Assert
        assertEquals(responseEntity.statusCode.value(), HttpStatus.BAD_REQUEST.value())
        assertEquals(responseEntity.body?.isSuccess, false)
        assertNotNull(responseEntity.body?.message)
    }
}