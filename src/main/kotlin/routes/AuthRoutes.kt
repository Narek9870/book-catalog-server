package com.example.routes

import com.example.models.AuthResponse
import com.example.models.UserCredentials
import com.example.service.AuthService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.authRoutes(authService: AuthService) {
    routing {
        post("/register") {
            val credentials = call.receive<UserCredentials>()

            authService.register(credentials).fold(
                onSuccess = { token -> call.respond(HttpStatusCode.Created, AuthResponse(token)) },
                onFailure = { error -> call.respond(HttpStatusCode.Conflict, error.message ?: "Ошибка") }
            )
        }

        post("/login") {
            val credentials = call.receive<UserCredentials>()

            authService.login(credentials).fold(
                onSuccess = { token -> call.respond(HttpStatusCode.OK, AuthResponse(token)) },
                onFailure = { error -> call.respond(HttpStatusCode.Unauthorized, error.message ?: "Ошибка") }
            )
        }
    }
}