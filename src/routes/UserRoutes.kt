package com.example.routes

import com.example.API_VERSION
import com.example.auth.JwtService
import com.example.auth.MySession
import com.example.respository.Repository
import com.sun.javafx.tools.packager.Param
import io.ktor.application.application
import io.ktor.application.call
import io.ktor.application.log
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location
import io.ktor.locations.post
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.sessions.sessions
import io.ktor.sessions.set

// Here are the routes
const val USERS = "$API_VERSION/users"
const val USER_LOGIN = "$USERS/login"
const val USER_CREATE = "$USERS/create"

@KtorExperimentalLocationsAPI
@Location(USER_LOGIN)
class UserLoginRoute

@KtorExperimentalLocationsAPI // Removes compiler warnings (It's experimental)
@Location(USER_CREATE)
class UserCreateRoute

fun Route.users( // Extension function to Routes
        db: Repository,
        jwtService: JwtService,
        hashFunction: (String) -> String
) {
    post<UserCreateRoute> { // Generates route for creating new users
        val signupParameters = call.receive<Parameters>() // Uses the call to get the parameters with the request

        val password = signupParameters["password"] // Looks for password parameter
                ?: return@post call.respond(
                        HttpStatusCode.Unauthorized, "Missing Fields")

        val displayName = signupParameters["displayName"]
                ?: return@post call.respond(
                        HttpStatusCode.Unauthorized, "Missing Fields")

        val email = signupParameters["email"]
                ?: return@post call.respond(
                        HttpStatusCode.Unauthorized, "Missing Fields")

        val hash = hashFunction(password) // Produces a hash string from the password

        try {
            val newUser = db.addUser(email, displayName, hash) // Adds a new user to the database
            newUser?.userId?.let {
                call.sessions.set(MySession(it))
                call.respondText(
                        jwtService.generateToken(newUser),
                        status = HttpStatusCode.Created
                )
            }
        } catch (e: Throwable) {
            application.log.error("Failed to register user", e)
            call.respond(HttpStatusCode.BadRequest, "Problems creating User")
        }
    }
}