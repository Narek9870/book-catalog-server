package com.example

import com.example.db.DatabaseFactory // убедись, что добавился импорт
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    // Подключаем базу данных при старте
    DatabaseFactory.init()

    // дальше идут стандартные настройки Ktor (configureRouting и т.д.)
    configureSerialization()
    configureRouting()
}