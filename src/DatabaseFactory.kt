package com.example

import com.example.respository.Todos
import com.example.respository.Users
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {
        Database.connect(hikari()) // Expose library, which uses Hikari to connect

        transaction {
            // Will only create tables if they don't already exist
            SchemaUtils.create(Users)
            SchemaUtils.create(Todos)
        }
    }

    private fun hikari(): HikariDataSource {

        // 1. Get the environment variables to set up the config
        val config = HikariConfig()
        config.driverClassName = System.getenv("JDBC_DRIVER")
        config.jdbcUrl = System.getenv("JDBC_DATABASE_URL")
        config.maximumPoolSize = 3
        config.isAutoCommit = false
        config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"

        // Set the user (only applicable if you don't have a user already, e.g. if you deploy it to Heruko)
        val user = System.getenv("DB_USER")
        if (user != null) {
            config.username = user
        }

        // Set the password (only applicable if you don't have a user already, e.g. if you deploy it to Heruko)
        val password = System.getenv("DB_PASSWORD")
        if (password != null) {
            config.password = password
        }

        config.validate() // Validate config
        return HikariDataSource(config)
    }

    // Declares a helper function to wrap a database call in a transaction and run it on an IO thread using Coroutines
    suspend fun <T> dbQuery(block: () -> T): T =
        withContext(Dispatchers.IO) {
            transaction { block() }
        }
}