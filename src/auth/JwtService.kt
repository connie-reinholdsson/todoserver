package com.example.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.example.models.User
import java.util.*

class JwtService {

    private val issuer = "todoServer"
    private val jwtSecret = System.getenv("JWT_SECRET") // 1
    private val algorithm = Algorithm.HMAC512(jwtSecret)

    // 2
    val verifier: JWTVerifier = JWT
            .require(algorithm)
            .withIssuer(issuer)
            .build()

    // Generate token to identify the user
    fun generateToken(user: User): String = JWT.create()
            .withSubject("Authentication")
            .withIssuer(issuer)
            .withClaim("id", user.userId)
            .withExpiresAt(expiresAt())
            .sign(algorithm)

    private fun expiresAt() =
            Date(System.currentTimeMillis() + 3_600_000 * 24) // 24 hours
}