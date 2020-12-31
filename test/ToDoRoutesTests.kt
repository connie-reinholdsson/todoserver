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

class ToDoRoutesTests {

    private val repository = mockk<TodoRepository>(relaxed = true)

    private val email = "email@email.com"
    private val displayName = "displayName"
    private val passwordHash = "passwordHash"
    private val todo = "1: Go for a walk and coffee!"

    private val user = User(123, email, displayName, passwordHash)

    // Adding a task

    @Test
    fun test_userAuthenticated_addToDo_correctPath_post_success() {
        testApp {
            //TODO Authenticate user

            coEvery { repository.findUser(any()) } returns user

            handleRequest(HttpMethod.Post, "/v1/todos") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                setBody(listOf("todo" to todo).formUrlEncode())
            }.apply {
                assertEquals(HttpStatusCode.Created, response.status())
                assertEquals("Added 1: Go for a walk and coffee! to list!", response.content)
            }
        }
    }

    //TODO Redirect to login page?
    @Test
    fun test_userNotAuthenticated_addToDo_correctPath_post_redirectToLoginPage() {
        testApp {
            //TODO Non authenticated user

            coEvery { repository.findUser(any()) } returns user

            handleRequest(HttpMethod.Post, "/v1/todos") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                setBody(listOf("todo" to todo).formUrlEncode())
            }.apply {
                assertEquals(HttpStatusCode.Unauthorized, response.status())
            }
        }
    }

    @Test
    fun test_userNotFound_addToDo_correctPath_post_redirectToLoginPage() {
        testApp {
            //TODO Authenticate user

            coEvery { repository.findUser(any()) } returns null

            handleRequest(HttpMethod.Post, "/v1/todos") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                setBody(listOf("todo" to todo).formUrlEncode())
            }.apply {
                assertEquals(HttpStatusCode.Unauthorized, response.status())
            }
        }
    }

    @Test
    fun test_userAuthenticated_addToDo_correctPath_post_missingFields() {
        testApp {
            //TODO Authenticate user

            coEvery { repository.findUser(any()) } returns user

            handleRequest(HttpMethod.Post, "/v1/todos") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                setBody(emptyList<Pair<String, String>>().formUrlEncode())
            }.apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())
                assertEquals("Missing Todo", response.content)
            }
        }
    }

    @Test
    fun test_userAuthenticated_addToDo_correctPath_get_errorResponse() {
        testApp {
            //TODO Authenticate user

            coEvery { repository.findUser(any()) } returns user

            handleRequest(HttpMethod.Get, "/v1/todos") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                setBody(emptyList<Pair<String, String>>().formUrlEncode())
            }.apply {
                assertFalse(requestHandled)
//                assertEquals(HttpStatusCode.BadRequest, response.status())
//                assertEquals("Missing Todo", response.content)
            }
        }
    }

    @Test
    fun test_userAuthenticated_addToDo_wrongPath_post_errorResponse() {
        testApp {
            //TODO Authenticate user

            coEvery { repository.findUser(any()) } returns user

            handleRequest(HttpMethod.Post, "/v1/todos/testt") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                setBody(emptyList<Pair<String, String>>().formUrlEncode())
            }.apply {
                assertFalse(requestHandled)
//                assertEquals(HttpStatusCode.BadRequest, response.status())
//                assertEquals("Missing Todo", response.content)
            }
        }
    }

    @Test
    fun test_userAuthenticated_addToDo_correctPath_post_exception_errorResponse() {
        testApp {
            //TODO Authenticate user
            //TODO Throw exception when adding todo

            coEvery { repository.findUser(any()) } returns user

            handleRequest(HttpMethod.Post, "/v1/todos/testt") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                setBody(emptyList<Pair<String, String>>().formUrlEncode())
            }.apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())
                assertEquals("Problems Saving Todo", response.content)
            }
        }
    }

    // Getting tasks

    @Test
    fun test_userFound_getTasks_correctPath_get_success() {
        testApp {
            coEvery { repository.findUser(any()) } returns user

            handleRequest(HttpMethod.Get, "/v1/todos") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
            }
        }
    }

    @Test
    fun test_userNotFound_getTasks_correctPath_get_redirectToLoginPage() {
        testApp {
            coEvery { repository.findUser(any()) } returns null

            handleRequest(HttpMethod.Get, "/v1/todos") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
            }.apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())
                assertEquals("Problems retrieving User", response.content)
            }
        }
    }

    @Test
    fun test_userNotFound_getTasks_correctPath_post_errorResponse() {
        testApp {
            coEvery { repository.findUser(any()) } returns user

            handleRequest(HttpMethod.Post, "/v1/todos") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
            }.apply {
                assertFalse(requestHandled)
//                assertEquals(HttpStatusCode.BadRequest, response.status())
//                assertEquals("Problems retrieving User", response.content)
            }
        }
    }

    @Test
    fun test_userNotFound_getTasks_wrongPath_get_errorResponse() {
        testApp {
            coEvery { repository.findUser(any()) } returns user

            handleRequest(HttpMethod.Get, "/v1/todos/test") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
            }.apply {
                assertFalse(requestHandled)
//                assertEquals(HttpStatusCode.BadRequest, response.status())
//                assertEquals("Problems retrieving User", response.content)
            }
        }
    }

    @Test
    fun test_userNotFound_getTasks_correctPath_get_exception() {
        testApp {
            //TODO Throw exception when getting users

            coEvery { repository.findUser(any()) } returns user

            handleRequest(HttpMethod.Get, "/v1/todos") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
            }.apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())
                assertEquals("Problems getting Todos", response.content)
            }
        }
    }
}