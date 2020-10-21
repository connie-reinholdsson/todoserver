package com.example.respository

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

// Database
object Users : Table() {
    val userId : Column<Int> = integer("id").autoIncrement().primaryKey()
    val email = varchar("email", 128).uniqueIndex()
    val displayName = varchar("display_name", 256)
    val passwordHash = varchar("password_hash", 64)
}

object Todos: Table() {
    val id : Column<Int> = integer("id").autoIncrement().primaryKey()
    val userId : Column<Int> = integer("userId").references(Users.userId)
    val todo = varchar("todo", 512)
    val done = bool("done")
}