package com.example.auth

import io.ktor.util.KtorExperimentalAPI
import io.ktor.util.hex
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@KtorExperimentalAPI // To avoid warnings for using hex
val hashKey = hex(System.getenv("SECRET_KEY")) // Use secrets

@KtorExperimentalAPI
val hmacKey = SecretKeySpec(hashKey, "HmacSHA1") // Creates a secret key using the given algorithm

@KtorExperimentalAPI
fun hash(password: String): String { // Converts password to string hash
    val hmac = Mac.getInstance("HmacSHA1")
    hmac.init(hmacKey)
    return hex(hmac.doFinal(password.toByteArray(Charsets.UTF_8)))
}