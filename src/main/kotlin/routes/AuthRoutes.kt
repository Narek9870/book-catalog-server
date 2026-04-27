package com.example.routes

import com.example.db.Users
import com.example.models.AuthResponse
import com.example.models.UserCredentials
import com.example.utils.JwtConfig
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt

fun Application.authRoutes() {
    routing {

        // --- РЕГИСТРАЦИЯ ---
        post("/register") {
            val credentials = call.receive<UserCredentials>()

            // 1. Проверяем, есть ли уже такой email в базе
            val existingUser = transaction {
                Users.select { Users.email eq credentials.email }.singleOrNull()
            }
            if (existingUser != null) {
                call.respond(HttpStatusCode.Conflict, "Пользователь с таким email уже существует")
                return@post
            }

            // 2. Шифруем (хэшируем) пароль
            val hashedPassword = BCrypt.hashpw(credentials.password, BCrypt.gensalt())

            // 3. Сохраняем пользователя в БД и получаем его новый ID
            val newUserId = transaction {
                Users.insert {
                    it[email] = credentials.email
                    it[passwordHash] = hashedPassword
                }[Users.id]
            }

            // 4. Генерируем токен и отправляем клиенту
            val token = JwtConfig.generateToken(newUserId)
            call.respond(HttpStatusCode.Created, AuthResponse(token))
        }

        // --- ЛОГИН ---
        post("/login") {
            val credentials = call.receive<UserCredentials>()

            // 1. Ищем пользователя по email
            val user = transaction {
                Users.select { Users.email eq credentials.email }.singleOrNull()
            }
            if (user == null) {
                call.respond(HttpStatusCode.Unauthorized, "Неверный email или пароль")
                return@post
            }

            // 2. Проверяем, совпадает ли пароль
            val passwordMatch = BCrypt.checkpw(credentials.password, user[Users.passwordHash])
            if (!passwordMatch) {
                call.respond(HttpStatusCode.Unauthorized, "Неверный email или пароль")
                return@post
            }

            // 3. Если всё ок, генерируем токен
            val token = JwtConfig.generateToken(user[Users.id])
            call.respond(HttpStatusCode.OK, AuthResponse(token))
        }
    }
}