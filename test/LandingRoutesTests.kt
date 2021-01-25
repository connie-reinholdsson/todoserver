package com.example

import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class LandingRoutesTests {

    @Test
    fun test_correctPath_get_success() {
        testApp {
            handleRequest(HttpMethod.Get, "/v1/").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("New here? Create an account to add some todos!", response.content)
            }
        }
    }

    //TODO Figure out how to throw exceptions
    @Test
    fun test_correctPath_get_error() {
        testApp {
            val error = mockk<Exception>()
            handleRequest(HttpMethod.Get, "/v1/").apply {
                assertEquals(HttpStatusCode.NotFound, response.status())
                assertEquals("Failed to show landing page", response.content)
            }
        }
    }

    @Test
    fun test_correctPath_post_notFound() {
        testApp {
            handleRequest(HttpMethod.Post, "/v1/").apply {
                assertFalse(requestHandled)
            }
        }
    }

    @Test
    fun test_wrongPath_get_notFound() {
        testApp {
            handleRequest(HttpMethod.Get, "/v1/1").apply {
                assertFalse(requestHandled)
            }
        }
    }
}
