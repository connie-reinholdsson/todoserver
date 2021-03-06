package com.example.routes

import com.example.API_VERSION
import com.example.auth.MySession
import com.example.respository.Repository
import io.ktor.application.application
import io.ktor.application.call
import io.ktor.application.log
import io.ktor.auth.authenticate
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location
import io.ktor.locations.get
import io.ktor.locations.post
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.sessions.get
import io.ktor.sessions.sessions

const val TODOS = "$API_VERSION/todos"
const val TODOS_REMOVE_ALL = "$API_VERSION/todos/removeAll"

@KtorExperimentalLocationsAPI
@Location(TODOS)
class ToDoRoute

@KtorExperimentalLocationsAPI
@Location(TODOS_REMOVE_ALL)
class ToDosRemoveAll

fun Route.todos(db: Repository) {
    authenticate("jwt") {// Authenticate these routes
        post<ToDoRoute> { // Define new route
            val todosParameters = call.receive<Parameters>()
            val todo = todosParameters["todo"]
                    ?: return@post call.respond(
                            HttpStatusCode.BadRequest, "Missing Todo") // If no text, throw error.

            val done = todosParameters["done"] ?: "false"

            // Check if the user has a session
            val user = call.sessions.get<MySession>()?.let {
                db.findUser(it.userId)
            }

            if (user == null) {
                call.respond(HttpStatusCode.BadRequest, "Problem retrieving user")
                return@post
            }

            try {
                // Adds the Todo to the database
                val currentTodo = db.addTodo(
                        user.userId, todo, done.toBoolean())

                currentTodo?.id?.let {
                    call.respond(HttpStatusCode.Created, currentTodo)
                }
            } catch (e: Throwable) {
                application.log.error("Failed to add todo", e)
                call.respond(HttpStatusCode.BadRequest, "Problems Saving Todo")
            }
        }

        get<ToDoRoute> {
            val user = call.sessions.get<MySession>()?.let { db.findUser(it.userId) }
                if (user == null) {
                    call.respond(HttpStatusCode.BadRequest, "Problems retrieving User")
                    return@get
                }

                try {
                    val todos = db.getTodos(user.userId)
                    call.respond(todos)
                } catch (e: Throwable) {
                    application.log.error("Failed to get Todos", e)
                    call.respond(HttpStatusCode.BadRequest, "Problems getting Todos")
                }
        }

        post<ToDosRemoveAll> {
            val user = call.sessions.get<MySession>()?.let { db.findUser(it.userId) }
            if (user == null) {
                call.respond(HttpStatusCode.BadRequest, "Problems retrieving User")
                return@post
            }

            try {
                db.removeAllTodos()
                call.respond(HttpStatusCode.OK, "Successfully removed all todos from database")
                return@post
            } catch (e: Throwable) {
                application.log.error("Failed to remove todos", e)
                call.respond(HttpStatusCode.BadRequest, "Failed to remove todos")
            }
        }
    }
}