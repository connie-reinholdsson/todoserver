package com.example.models

import io.ktor.auth.Principal
import java.io.Serializable

data class User(
    val userId: Int,
    val email: String,
    val displayName: String,
    val passwordHash: String
) : Serializable, Principal // Java security

// Tied to the userId
data class Todo(
    val id: Int,
    val userId: Int, // Ties it to the user id
    val todo: String,
    val done: Boolean
)
