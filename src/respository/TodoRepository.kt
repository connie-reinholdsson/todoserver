package com.example.respository

import com.example.DatabaseFactory.dbQuery
import com.example.models.Todo
import com.example.models.User
import com.oracle.tools.packager.StandardBundlerParam.VERBOSE
import jdk.internal.instrumentation.Logger
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.InsertStatement
import sun.rmi.runtime.Log
import sun.rmi.runtime.Log.BRIEF
import sun.rmi.runtime.Log.VERBOSE
import java.rmi.server.LogStream.BRIEF
import java.rmi.server.LogStream.VERBOSE
import java.util.logging.Logger.GLOBAL_LOGGER_NAME

class TodoRepository : Repository {

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

    override suspend fun removeUser(userId: Int): Unit = dbQuery {
        Users.deleteWhere {
            Users.userId.eq(userId) }
        }

    override suspend fun removeAllUsers(): Unit = dbQuery {
        val allEmails = Users.email
        println("Connie ${Users.select { 
            Users.userId.eq(0)
        }}")
        println("Connie ${Users.selectAll().groupedByColumns}")
        Users.deleteAll()
    }

    // Defines addTodo, which takes a userId, the text and done flag
    override suspend fun addTodo(userId: Int, todo: String, done: Boolean): Todo? {
        var statement : InsertStatement<Number>? = null
        dbQuery {
            statement = Todos.insert {
                it[Todos.userId] = userId
                it[Todos.todo] = todo
                it[Todos.done] = done
            }
        }
        return rowToTodo(statement?.resultedValues?.get(0))
    }

    // Defines the method to get todos for a given user id
    override suspend fun getTodos(userId: Int): List<Todo> {
        return dbQuery {
            Todos.select {
                Todos.userId.eq((userId))
            }.mapNotNull {
                rowToTodo(it)
            }
        }
    }

    override suspend fun removeAllTodos() {
        return dbQuery {
            Todos.deleteAll()
        }
    }

    private fun rowToTodo(row: ResultRow?): Todo? {
        if (row == null) {
            return null
        }
        return Todo(
                id = row[Todos.id],
                userId = row[Todos.userId],
                todo = row[Todos.todo],
                done = row[Todos.done]
        )
    }

    private fun rowToUser(row: ResultRow?): User? {
        println("Connie $row")
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