package com.example

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        // Стартовая страница сервера (Health-check)
        get("/") {
            call.respondText(" Book Catalog Server is running API successfully!")
        }
    }
}