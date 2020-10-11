package com.example.models

import java.io.Serializable
import java.security.Principal

data class User(
    val userId: Int,
    val email: String,
    val displayName: String,
    val passwordHash: String
) : Serializable, Principal // Java security
{
    override fun getName(): String {
        // Required by Principal
        return "Connie"
    }
}

data class Todo(
    val id: Int,
    val userId: Int, // Ties it to the user id
    val todo: String,
    val done: Boolean
)
