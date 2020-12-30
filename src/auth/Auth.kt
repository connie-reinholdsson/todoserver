package com.example.auth

import io.ktor.config.ApplicationConfig
import io.ktor.util.KtorExperimentalAPI
import io.ktor.util.hex
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

// Passing in application config to access secrets (previously used System.getenv("SECRET_KEY") however
// cannot mock this in tests without using PowerMockito
@KtorExperimentalAPI
fun hash(applicationConfig: ApplicationConfig, password: String): String { // Converts password to string hash
    val secretInfo = applicationConfig.config("secret")
    val secretKey: String = secretInfo.property("key").getString()

    val hashKey = hex(secretKey)
    val hmacKey = SecretKeySpec(hashKey, "HmacSHA1") // Creates a secret key using the given algorithm

    val hmac = Mac.getInstance("HmacSHA1")
    hmac.init(hmacKey)
    return hex(hmac.doFinal(password.toByteArray(Charsets.UTF_8)))
}