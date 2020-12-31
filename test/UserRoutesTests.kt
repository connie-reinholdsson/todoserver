package com.example

import com.example.models.User
import com.example.respository.TodoRepository
import io.ktor.http.*
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.mockk.coEvery
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class UserRoutesTests {

    private val repository = mockk<TodoRepository>(relaxed = true)

    private val email = "email@email.com"
    private val displayName = "displayName"
    private val password = "Password1"
    private val passwordHash = "passwordHash"

    private val user = User(123, email, displayName, passwordHash)

    // Create account

    @Test
    fun test_createAccount_correctPath_post_success() {
        testApp {
            coEvery { repository.addUser(any(), any(), any()) } returns user

//            runBlocking {
//                repository.addUser(email, displayName, passwordHash)
//            }

            handleRequest(HttpMethod.Post, "/v1/users/create/nonexistentpath") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                setBody(listOf("email" to email, "displayName" to displayName, "password" to password).formUrlEncode())
            }.apply {
                assertEquals(HttpStatusCode.Created, response.status())
                assertEquals("New user created eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJBdXRoZW50aWNhdGlvbiIsImlzcyI6InRvZG9TZXJ2ZXIiLCJpZCI6MjksImV4cCI6MTYwOTQzMDk3Mn0.iJJ3wEmC_iMkW138OePDdAgGwWCQTCJkcDPupVOXDgfD7MxkVPJ9XnLKw4hDEa-RBViSR1MeVcS3eit-5RRvCA", response.content)
            }
        }
    }

    // Create account - Missing fields

    @Test
    fun test_createAccount_correctPath_post_missingEmail() {
        testApp {
            coEvery { repository.addUser(any(), any(), any()) } returns user

            handleRequest(HttpMethod.Post, "/v1/users/create") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                setBody(listOf("displayName" to displayName, "password" to password).formUrlEncode())
            }.apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())
                assertEquals("Missing Fields: Email", response.content)
            }
        }
    }

    @Test
    fun test_createAccount_correctPath_post_missingDisplayName() {
        testApp {
            coEvery { repository.addUser(any(), any(), any()) } returns user

            handleRequest(HttpMethod.Post, "/v1/users/create") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                setBody(listOf("email" to email, "password" to password).formUrlEncode())
            }.apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())
                assertEquals("Missing Fields: Display name", response.content)
            }
        }
    }

    @Test
    fun test_createAccount_correctPath_post_missingPassword() {
        testApp {
            coEvery { repository.addUser(any(), any(), any()) } returns user

            handleRequest(HttpMethod.Post, "/v1/users/create") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                setBody(listOf("email" to email, "displayName" to displayName).formUrlEncode())
            }.apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())
                assertEquals("Missing Fields: Password", response.content)
            }
        }
    }

    // Create account - Error handling // TODO Improve error handling here?

    @Test
    fun test_createAccount_correctPath_get_errorResponse() {
        testApp {
            coEvery { repository.addUser(any(), any(), any()) } returns user

            handleRequest(HttpMethod.Get, "/v1/users/create") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                setBody(listOf("email" to email, "displayName" to displayName, "password" to password).formUrlEncode())
            }.apply {
                assertFalse(requestHandled)
//                assertEquals(HttpStatusCode.NotFound, response.status())
//                assertEquals("Oops this page doesn't exist!", response.content)
            }
        }
    }

    @Test
    fun test_createAccount_wrongPath_post_errorResponse() {
        testApp {
            coEvery { repository.addUser(any(), any(), any()) } returns user

            handleRequest(HttpMethod.Get, "/v1/users/create/test") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                setBody(listOf("email" to email, "displayName" to displayName, "password" to password).formUrlEncode())
            }.apply {
                assertFalse(requestHandled)
//                assertEquals(HttpStatusCode.NotFound, response.status())
//                assertEquals("Oops this page doesn't exist!", response.content)
            }
        }
    }

    @Test
    fun test_createAccount_correctPath_post_errorResponse() {
        testApp {
            coEvery { repository.addUser(any(), any(), any()) } returns user

            handleRequest(HttpMethod.Post, "/v1/users/create") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                setBody(listOf("email" to email, "displayName" to displayName, "password" to password).formUrlEncode())
            }.apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())
                assertEquals("Problems creating User", response.content)
            }
        }
    }

    // Login

    @Test
    fun test_login_correctPath_Post_Success() {
        testApp {
            coEvery { repository.findUserByEmail(any()) } returns user

            handleRequest(HttpMethod.Post, "/v1/users/login") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                setBody(listOf("email" to email, "password" to password).formUrlEncode())
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
//                assertEquals("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJBdXRoZW50aWNhdGlvbiIsImlzcyI6InRvZG9TZXJ2ZXIiLCJpZCI6MjksImV4cCI6MTYwOTQzMDk3Mn0.iJJ3wEmC_iMkW138OePDdAgGwWCQTCJkcDPupVOXDgfD7MxkVPJ9XnLKw4hDEa-RBViSR1MeVcS3eit-5RRvCA", response.content)
            }
        }
    }

    // Login - Missing fields

    @Test
    fun test_login_correctPath_Post_missingEmail() {
        testApp {
            coEvery { repository.findUserByEmail(any()) } returns user

            handleRequest(HttpMethod.Post, "/v1/users/login") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                setBody(listOf("password" to password).formUrlEncode())
            }.apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())
                assertEquals("Missing Fields: Email", response.content)
            }
        }
    }

    @Test
    fun test_login_correctPath_Post_missingPassword() {
        testApp {
            coEvery { repository.findUserByEmail(any()) } returns user

            handleRequest(HttpMethod.Post, "/v1/users/login") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                setBody(listOf("email" to email).formUrlEncode())
            }.apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())
                assertEquals("Missing Fields: Password", response.content)
            }
        }
    }

    // Login - Error handling // TODO Improve error handling here?

    @Test
    fun test_login_correctPath_get_errorResponse() {
        testApp {
            coEvery { repository.findUserByEmail(any()) } returns user

            handleRequest(HttpMethod.Get, "/v1/users/login") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                setBody(listOf("email" to email, "password" to password).formUrlEncode())
            }.apply {
                assertFalse(requestHandled)
//                assertEquals(HttpStatusCode.NotFound, response.status())
//                assertEquals("Oops this page doesn't exist!", response.content)
            }
        }
    }

    @Test
    fun test_login_wrongPath_post_errorResponse() {
        testApp {
            coEvery { repository.findUserByEmail(any()) } returns user

            handleRequest(HttpMethod.Post, "/v1/users/login/test") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                setBody(listOf("email" to email, "displayName" to displayName, "password" to password).formUrlEncode())
            }.apply {
                assertFalse(requestHandled)
//                assertEquals(HttpStatusCode.NotFound, response.status())
//                assertEquals("Oops this page doesn't exist!", response.content)
            }
        }
    }

    @Test
    fun test_login_correctPath_post_errorResponse() {
        testApp {
            coEvery { repository.findUserByEmail(any()) } returns user

            handleRequest(HttpMethod.Post, "/v1/users/login") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                setBody(listOf("email" to email, "password" to password).formUrlEncode())
            }.apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())
                assertEquals("Problems retrieving User", response.content)
            }
        }
    }

    // Logout

    @Test
    fun test_logout_correctPath_post_success() {
        testApp {
            // Return a valid session
            // When db.findUser return user id

            // Successfully clear session

            coEvery { repository.findUser(any()) } returns user

            handleRequest(HttpMethod.Post, "/v1/users/logout") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
//                setBody(listOf("email" to email, "displayName" to displayName, "password" to password).formUrlEncode())
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("Successfully signed out ${email}!", response.content)
            }
        }
    }

    @Test
    fun test_logout_correctPath_post_userNotFound() {
        testApp {
            coEvery { repository.findUser(any()) } returns null

            handleRequest(HttpMethod.Post, "/v1/users/logout") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
//                setBody(listOf("email" to email, "displayName" to displayName, "password" to password).formUrlEncode())
            }.apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())
                assertEquals("Problem retrieving current user", response.content)
            }
        }
    }

    @Test
    fun test_logout_correctPath_get_errorResponse() {
        testApp {
            coEvery { repository.findUser(any()) } returns user

            handleRequest(HttpMethod.Get, "/v1/users/logout") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
//                setBody(listOf("email" to email, "displayName" to displayName, "password" to password).formUrlEncode())
            }.apply {
                assertFalse(requestHandled)
//                assertEquals(HttpStatusCode.BadRequest, response.status())
//                assertEquals("Problem signing out user ${email}.", response.content)
            }
        }
    }

    @Test
    fun test_logout_wrongPath_post_errorResponse() {
        testApp {
            coEvery { repository.findUser(any()) } returns user

            handleRequest(HttpMethod.Post, "/v1/users/logout/test") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
//                setBody(listOf("email" to email, "displayName" to displayName, "password" to password).formUrlEncode())
            }.apply {
                assertFalse(requestHandled)
//                assertEquals(HttpStatusCode.BadRequest, response.status())
//                assertEquals("Problem signing out user ${email}.", response.content)
            }
        }
    }

    @Test
    fun test_logout_correctPath_post_errorResponse() {
        testApp {
            coEvery { repository.findUser(any()) } returns user

            handleRequest(HttpMethod.Post, "/v1/users/logout") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
//                setBody(listOf("email" to email, "displayName" to displayName, "password" to password).formUrlEncode())
            }.apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())
                assertEquals("Problem signing out user ${email}.", response.content)
            }
        }
    }

}

