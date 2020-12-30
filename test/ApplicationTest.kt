package com.example

import io.ktor.config.MapApplicationConfig
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.powermock.api.mockito.PowerMockito
import java.lang.System.getenv
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
}
