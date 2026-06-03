package com.example

import com.example.db.DatabaseFactory
import io.ktor.server.application.*
import com.example.routes.authRoutes
import com.example.routes.bookRoutes
import com.example.plugins.configureSecurity
import com.example.repository.BookRepository
import com.example.repository.UserRepository
import com.example.service.AuthService
import io.ktor.server.plugins.calllogging.*
import org.slf4j.event.Level

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    // 1. Инициализация БД
    DatabaseFactory.init()

    // 2. Внедрение зависимостей
    val userRepository = UserRepository()
    val authService = AuthService(userRepository)
    val bookRepository = BookRepository()

    // Включаем логирование всех запросов в консоль
    install(CallLogging) {
        level = Level.INFO
    }

    // 3. Плагины Ktor
    configureSecurity()
    configureSerialization()
    configureRouting()

    // 4. Подключение маршрутов
    authRoutes(authService)
    bookRoutes(bookRepository)
}