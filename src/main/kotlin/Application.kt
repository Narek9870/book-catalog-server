package com.example

import com.example.db.DatabaseFactory
import io.ktor.server.application.*
import com.example.routes.authRoutes

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    DatabaseFactory.init()
    configureSerialization()
    configureRouting()

    // Подключаем наши пути для авторизации:
    authRoutes()
}