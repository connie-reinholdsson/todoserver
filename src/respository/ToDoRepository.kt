package com.example.respository

import com.example.DatabaseFactory.dbQuery
import com.example.models.User
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.InsertStatement

class ToDoRepository : Repository {

    override suspend fun addUser(email: String, displayName: String, passwordHash: String): User? {
        var statement: InsertStatement<Number>? = null // An Exposed class which helps with inserting the data
        dbQuery { // Inserts new user record
            statement = Users.insert { user ->
                user[Users.email] = email
                user[Users.displayName] = displayName
                user[Users.passwordHash] = passwordHash
            }
        }

        return rowToUser(statement?.resultedValues?.get(0)) // Convert ResultRow to User class
    }

    override suspend fun findUser(userId: Int) = dbQuery {
        Users.select { Users.userId.eq(userId) }
            .map { rowToUser(it) }.singleOrNull()
    }

    override suspend fun findUserByEmail(email: String) = dbQuery {
        Users.select { Users.email.eq(email) }
            .map { rowToUser(it) }.singleOrNull()
    }

    private fun rowToUser(row: ResultRow?): User? {
        if (row == null) {
            return null
        }

        return User(
            userId = row[Users.userId],
            email = row[Users.email],
            displayName = row[Users.displayName],
            passwordHash = row[Users.passwordHash]
        )
    }

}