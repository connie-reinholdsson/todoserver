package com.example

import io.ktor.config.MapApplicationConfig
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {
    // Landing route tests
    @Test
    fun testRequests() {
        testApp {
            handleRequest(HttpMethod.Get, "/v1/").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("New here? Create an account to add some todos!", response.content)
            }
        }
    }

    /**
     * Convenience method we use to configure a test application and to execute a [callback] block testing it.
     */
    private fun testApp(callback: TestApplicationEngine.() -> Unit): Unit {
        try {
            withTestApplication({
                (environment.config as MapApplicationConfig).apply {
                    put("todoserver.jdbcDriver.key", "org.postgresql.Driver")
                    put("todoserver.databaseUrl.key", "jdbc:postgresql:todos?user=postgres")
                    put("todoserver.jwtSecret.key", "898748674728934843")
                    put("todoserver.secret.key", "898748674728934843")
                }
                module(testing = true)

        }, callback)
        } finally {
        }
    }

    fun TestApplicationEngine.cookiesSession(
        initialCookies: List<Cookie> = listOf(),
        callbackEnvironmentSetup: TestApplicationEngine.() -> Unit,
        callback: CookieTrackerTestApplicationEngine.() -> Unit
    ) {
        try {
            withTestApplication({
                (environment.config as MapApplicationConfig).apply {
                    put("todoserver.jdbcDriver.key", "org.postgresql.Driver")
                    put("todoserver.databaseUrl.key", "jdbc:postgresql:todos?user=postgres")
                    put("todoserver.jwtSecret.key", "898748674728934843")
                    put("todoserver.secret.key", "898748674728934843")
                }
                module(testing = true)

            }, callbackEnvironmentSetup)
        } finally {
        }
        callback(CookieTrackerTestApplicationEngine(this, initialCookies))
    }

    class CookieTrackerTestApplicationEngine(
        val engine: TestApplicationEngine,
        var trackedCookies: List<Cookie> = listOf()
    )

    fun CookieTrackerTestApplicationEngine.handleRequest(
        method: HttpMethod,
        uri: String,
        setup: TestApplicationRequest.() -> Unit = {}
    ): TestApplicationCall {


        return engine.handleRequest(method, uri) {
            val cookieValue = trackedCookies.map { (it.name).encodeURLParameter() + "=" + (it.value).encodeURLParameter() }.joinToString("; ")
            addHeader("Cookie", cookieValue)
            setup()
        }.apply {
            trackedCookies = response.headers.values("Set-Cookie").map { parseServerSetCookieHeader(it) }
        }
    }
}
