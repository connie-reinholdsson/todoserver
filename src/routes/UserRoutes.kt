package com.example.routes

import com.example.API_VERSION
import com.example.auth.JwtService
import com.example.auth.MySession
import com.example.respository.Repository
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
import io.ktor.sessions.clear
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import io.ktor.sessions.set

// Here are the routes
const val USERS = "$API_VERSION/users"
const val USER_LOGIN = "$USERS/login"
const val USER_CREATE = "$USERS/create"
const val USER_LOGOUT = "$USERS/logout"
const val USER_REMOVE = "$USERS/remove"
const val USER_REMOVE_ALL = "$USERS/removeall"

@KtorExperimentalLocationsAPI
@Location(USER_LOGIN)
class UserLoginRoute

@KtorExperimentalLocationsAPI
@Location(USER_LOGOUT)
class UserLogoutRoute

@KtorExperimentalLocationsAPI // Removes compiler warnings (It's experimental)
@Location(USER_CREATE)
class UserCreateRoute

@KtorExperimentalLocationsAPI // Removes compiler warnings (It's experimental)
@Location(USER_REMOVE)
class UserRemoveRoute

@KtorExperimentalLocationsAPI // Removes compiler warnings (It's experimental)
@Location(USER_REMOVE_ALL)
class UserRemoveAllRoute

fun Route.users( // Extension function to Routes
        db: Repository,
        jwtService: JwtService,
        hashFunction: (String) -> String
) {
    post<UserCreateRoute> { // Generates route for creating new users
        val signupParameters = call.receive<Parameters>() // Uses the call to get the parameters with the request

        val password = signupParameters["password"] // Looks for password parameter
                ?: return@post call.respond(
                        HttpStatusCode.BadRequest, "Missing Fields: Password")

        val displayName = signupParameters["displayName"]
                ?: return@post call.respond(
                        HttpStatusCode.BadRequest, "Missing Fields: Display name")

        val email = signupParameters["email"]
                ?: return@post call.respond(
                        HttpStatusCode.BadRequest, "Missing Fields: Email")

        val hash = hashFunction(password) // Produces a hash string from the password

        try {
            val newUser = db.addUser(email, displayName, hash) // Adds a new user to the database
            newUser?.userId?.let {
                call.sessions.set(MySession(it))
                call.respondText(
                        "New user created ${jwtService.generateToken(newUser)}",
                        status = HttpStatusCode.Created
                )
            }
        } catch (e: Throwable) {
            application.log.error("Failed to register user", e)
            call.respond(HttpStatusCode.BadRequest, "Problems creating User")
        }
    }

    post<UserLoginRoute> { // 1
        val signinParameters = call.receive<Parameters>()
        val password = signinParameters["password"]
            ?: return@post call.respond(
                HttpStatusCode.BadRequest, "Missing Fields: Password")
        val email = signinParameters["email"]
            ?: return@post call.respond(
                HttpStatusCode.BadRequest, "Missing Fields: Email")
        val hash = hashFunction(password)
        try {
            val currentUser = db.findUserByEmail(email) // 2
            currentUser?.userId?.let {
                if (currentUser.passwordHash == hash) { // 3
                    call.sessions.set(MySession(it)) // 4
                    call.respondText(jwtService.generateToken(currentUser)) // 5
                } else {
                    call.respond(
                        HttpStatusCode.BadRequest, "Problems retrieving User") // 6
                }
            }
        } catch (e: Throwable) {
            application.log.error("Failed to register user", e)
            call.respond(HttpStatusCode.BadRequest, "Problems retrieving User")
        }
    }

    post<UserLogoutRoute> {
        // Check if there is a current user session
        val user = call.sessions.get<MySession>()?.let {
            db.findUser(it.userId)
        }

        if (user == null) {
            call.respond(HttpStatusCode.BadRequest, "Problem retrieving current user")
            return@post
        }

        try {
            call.sessions.clear<MySession>()
            call.respondText("Successfully signed out ${user.email}!")
        } catch (e: Throwable) {
            application.log.error("Failed to sign out user", e)
            call.respond(HttpStatusCode.BadRequest, "Problem signing out user ${user.email}.")
        }
    }

    // Have to be signed in
    post<UserRemoveRoute> {
        // Check if there is a current user session
        val user = call.sessions.get<MySession>()?.let {
            db.removeUser(it.userId)
        }

        if (user == null) {
            call.respond(HttpStatusCode.BadRequest, "Problem retrieving current user")
            return@post
        }

        try {
            db.removeAllUsers()
            call.respond(HttpStatusCode.OK, "Successfully removed user from database")
            return@post
        }

        catch (e: Throwable) {
            application.log.error("Failed to remove user from database", e)
            call.respond(HttpStatusCode.InternalServerError, "Failed to remove user from database.")
        }
    }

    // Have to be signed in
    post<UserRemoveAllRoute> {
        // Check if there is a current user session
        try {
            db.removeAllUsers()
            call.respond(HttpStatusCode.OK, "Successfully removed all users from database")
            return@post
        }

        catch (e: Throwable) {
            application.log.error("Failed to remove users from database", e)
            call.respond(HttpStatusCode.InternalServerError, "Problem removing all users from database.")
        }
    }
}
