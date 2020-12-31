package com.example

import io.ktor.config.ApplicationConfig
import io.ktor.config.MapApplicationConfig
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.withTestApplication
import io.mockk.mockk

/**
 * Convenience method we use to configure a test application and to execute a [callback] block testing it.
 */

fun testApp(callback: TestApplicationEngine.() -> Unit): Unit {
    // Mock repository response
    val email = "email@email.com"
    val displayName = "displayName"
    val password = "Password1"
//    val passwordHash = "Password1Hashed"


//    val repo: Repository = mockk(relaxed = true)
    val config: ApplicationConfig = mockk()
    val passwordHash = "96f275da6392d7776348540277ad7a04c632fb18"


    try {
        withTestApplication({
//            coEvery { repo.addUser(any(), any(), any()) } returns User(123, email, displayName, passwordHash)

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