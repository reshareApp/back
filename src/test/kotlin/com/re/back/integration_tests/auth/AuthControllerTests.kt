package com.re.back.integration_tests.auth


import com.re.back.ApiCustomResponse
import com.re.back.auth.dtos.request.LoginRequestDto
import com.re.back.auth.dtos.request.RegisterRequestDto
import com.re.back.auth.entities.AppUser
import com.re.back.auth.enums.UserRole
import com.re.back.auth.repositories.AppUsersRepository
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
            )
        )


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
    fun `Login with not registered user name or email, returns 404 NOT_FOUND`() {
        // Arrange
        val uri = "http://localhost:$port$apiVersion/auth/login"
        val loginRequestDto = LoginRequestDto(
            "not valid email or user name",
            "PASSWORD_1234"
        )
        val httpRequestEntity = HttpEntity<LoginRequestDto>(loginRequestDto)


        // Act
        val responseEntity = restTemplate.postForEntity<ApiCustomResponse>(
            uri,
            httpRequestEntity,
            ApiCustomResponse::class
        )


        // Assert
        assertEquals(responseEntity.statusCode.value(), HttpStatus.NOT_FOUND.value())
        assertEquals(responseEntity.body?.isSuccess, false)
        assertNotNull(responseEntity.body?.message)
    }

    @Test
    fun `Login with week password, returns 400 BAD_REQUEST`() {
        // Arrange
        val uri = "http://localhost:$port$apiVersion/auth/login"
        val loginRequestDto = LoginRequestDto(
            "random email/user name",
            "week"
        )
        val httpRequestEntity = HttpEntity<LoginRequestDto>(loginRequestDto)


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
    fun `Login with wrong password, returns 400 BAD_REQUEST`() {
        val loginTest = LoginWithDifferentPasswordTest()
        loginTest.test()
    }

    @Test
    fun `Login with right credentials, returns 200 OK`() {
        val loginTest = LoginWithSamePasswordTest()
        loginTest.test()
    }


    private abstract inner class LoginTestTemplate {

        protected abstract val registerPassword: String
        protected abstract val loginPassword: String
        protected abstract val expectedStatusCode: HttpStatus
        protected abstract val isSuccess: Boolean

        fun test() {
            // Arrange
            val registerUri = "http://localhost:$port$apiVersion/auth/register"
            val loginUri = "http://localhost:$port$apiVersion/auth/login"

            val userName = "omar"
            val email = "omar@gmail.com"

            val registerRequestDto = RegisterRequestDto(
                email,
                userName,
                registerPassword
            )
            val httpRegisterRequestEntity = HttpEntity<RegisterRequestDto>(registerRequestDto)


            val loginRequestDto = LoginRequestDto(
                email,
                loginPassword
            )
            val httpLoginRequestEntity = HttpEntity<LoginRequestDto>(loginRequestDto)


            // Act
            restTemplate.postForEntity<ApiCustomResponse>(
                registerUri,
                httpRegisterRequestEntity,
                ApiCustomResponse::class
            )

            val loginResponseEntity = restTemplate.postForEntity<ApiCustomResponse>(
                loginUri,
                httpLoginRequestEntity,
                ApiCustomResponse::class
            )


            // Assert
            assertEquals(loginResponseEntity.statusCode.value(), expectedStatusCode.value())
            assertEquals(loginResponseEntity.body?.isSuccess, isSuccess)
        }
    }

    private inner class LoginWithSamePasswordTest : LoginTestTemplate() {
        override val registerPassword = "Omar123456"
        override val loginPassword = "Omar123456"
        override val expectedStatusCode: HttpStatus = HttpStatus.OK
        override val isSuccess: Boolean = true
    }

    private inner class LoginWithDifferentPasswordTest : LoginTestTemplate() {
        override val registerPassword = "Omar123456"
        override val loginPassword = "Ahmed123456"
        override val expectedStatusCode: HttpStatus = HttpStatus.BAD_REQUEST
        override val isSuccess: Boolean = false
    }
}