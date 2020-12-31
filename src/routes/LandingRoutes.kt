package com.example.routes

import com.example.API_VERSION
import io.ktor.application.application
import io.ktor.application.call
import io.ktor.application.log
import io.ktor.http.HttpStatusCode
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location
import io.ktor.locations.get
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Route

const val LANDING = "$API_VERSION/"

@KtorExperimentalLocationsAPI
@Location(LANDING)
class LandingRoute

fun Route.landing() {
    get<LandingRoute> {
        try {
            call.respondText("New here? Create an account to add some todos!", status = HttpStatusCode.OK)
        } catch (e: Throwable) {
            application.log.error("Failed to show landing page", e)
            call.respond(HttpStatusCode.NotFound, "Failed to show landing page")
        }
    }
}

