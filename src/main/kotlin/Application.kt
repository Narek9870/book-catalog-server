package com.example

import com.example.db.DatabaseFactory
import io.ktor.server.application.*
import com.example.routes.authRoutes
import com.example.routes.bookRoutes
import com.example.plugins.configureSecurity
import com.example.repository.BookRepositoryImpl
import com.example.repository.UserRepositoryImpl
import com.example.service.AuthService
import io.ktor.server.plugins.calllogging.*
import org.slf4j.event.Level

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    //Инициализация БД
    DatabaseFactory.init()

    // Внедрение зависимостей
    val userRepository = UserRepositoryImpl()
    val authService = AuthService(userRepository)
    val bookRepository = BookRepositoryImpl()

    // Включаем логирование всех запросов в консоль
    install(CallLogging) {
        level = Level.INFO
    }

    //Плагины Ktor
    configureSecurity()
    configureSerialization()
    configureRouting()

    //Подключение маршрутов
    authRoutes(authService)
    bookRoutes(bookRepository)
}