package com.example

import com.example.auth.JwtService
import com.example.auth.MySession
import com.example.auth.hash
import com.example.respository.TodoRepository
import com.example.routes.landing
import com.example.routes.todos
import com.example.routes.users
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.jwt.jwt
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.gson.gson
import io.ktor.http.HttpStatusCode
import io.ktor.locations.Locations
import io.ktor.response.respond
import io.ktor.routing.routing
import io.ktor.sessions.Sessions
import io.ktor.sessions.cookie
import kotlin.collections.set

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args) // Where it all starts

@Suppress("unused")
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    install(Locations) {
    }

    install(Sessions) {
        cookie<MySession>("MY_SESSION") {
            cookie.extensions["SameSite"] = "lax"
        }
    }

    // Moved to application.conf file to be able to mock the secrets in unit tests
    val config = environment.config.config("todoserver")
    val jdbcDriverInfo = config.config("jdbcDriver")
    val jdbcDriverKey: String = jdbcDriverInfo.property("key").getString()

    val databaseUrlInfo = config.config("databaseUrl")
    val databaseUrlKey: String = databaseUrlInfo.property("key").getString()

    // Initialise the database with keys
    DatabaseFactory.init(jdbcDriverKey, databaseUrlKey)
    val db = TodoRepository()

    // Initialises user authentication classes with keys
    val jwtSecretInfo = config.config("jwtSecret")
    val jwtSecretKey: String = jwtSecretInfo.property("key").getString()

    val jwtService = JwtService(jwtSecretKey)
    val hashFunction = { s: String -> hash(config, s) }

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

    install(StatusPages) {
        exception<Throwable> { cause ->
            call.respond(HttpStatusCode.InternalServerError)
        }
    }

    routing {
        // Set up routing
        users(db, jwtService, hashFunction)
        todos(db)
        landing()

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

