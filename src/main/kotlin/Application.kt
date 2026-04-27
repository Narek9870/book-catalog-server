package com.example

import com.example.db.DatabaseFactory
import io.ktor.server.application.*
import com.example.routes.authRoutes
import com.example.routes.bookRoutes
import com.example.plugins.configureSecurity

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    DatabaseFactory.init()

    // СНАЧАЛА включаем проверку токенов
    configureSecurity()

    configureSerialization()
    configureRouting()

    // Потом подключаем пути
    authRoutes()
    bookRoutes()
}