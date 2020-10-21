package com.example

import com.example.auth.JwtService
import com.example.auth.MySession
import com.example.auth.hash
import com.example.respository.TodoRepository
import com.example.routes.users
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.sessions.*
import io.ktor.auth.*
import io.ktor.auth.jwt.jwt
import io.ktor.gson.*
import io.ktor.features.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args) // Where it all starts

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    // Default code when creating the project
    install(Locations) {
    }

    install(Sessions) {
        cookie<MySession>("MY_SESSION") {
            cookie.extensions["SameSite"] = "lax"
        }
    }

    // Initialises the database
    DatabaseFactory.init()

    // Set up the repository
    val db = TodoRepository()

    // Initialises authentication classes
    val jwtService = JwtService()
    val hashFunction = { s: String -> hash(s) }

    install(Authentication) {
        jwt("jwt") { // Define the JWT
            verifier(jwtService.verifier) // Specifies the verifier we created
            realm = "Todo Server"
            validate { // Method that runs each time he app needs to authenticate a call
                val payload = it.payload
                val claim = payload.getClaim("id")
                val claimString = claim.asInt()
                val user = db.findUser(claimString) // Tries to find the user with the userId from the claimString
                user
            }
        }
    }

    install(ContentNegotiation) {
        gson {
        }
    }

    routing {

        // Set up the users
        users(db, jwtService, hashFunction)

        // Don't need this
//        get("/") {
//            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
//        }
//
//        get<MyLocation> {
//            call.respondText("Location: name=${it.name}, arg1=${it.arg1}, arg2=${it.arg2}")
//        }
//        // Register nested routes
//        get<Type.Edit> {
//            call.respondText("Inside $it")
//        }
//        get<Type.List> {
//            call.respondText("Inside $it")
//        }
//
//        get("/session/increment") {
//            val session = call.sessions.get<MySession>() ?: MySession()
//            call.sessions.set(session.copy(count = session.count + 1))
//            call.respondText("Counter is ${session.count}. Refresh to increment.")
//        }
//
//        get("/json/gson") {
//            call.respond(mapOf("hello" to "world"))
//        }
    }
}

const val API_VERSION = "/v1" // Prefix all paths in the route

// Don't need this
//@Location("/location/{name}")
//class MyLocation(val name: String, val arg1: Int = 42, val arg2: String = "default")
//
//@Location("/type/{name}") data class Type(val name: String) {
//    @Location("/edit")
//    data class Edit(val type: Type)
//
//    @Location("/list/{page}")
//    data class List(val type: Type, val page: Int)
//}

//@Location("route")


//data class MySession(val count: Int = 0)

