package com.example.models

import kotlinx.serialization.Serializable

// Этот класс сервер ждет от Android (когда пользователь вводит данные)
@Serializable
data class UserCredentials(
    val email: String,
    val password: String
)

// Этот класс сервер отправляет обратно в Android (в случае успеха)
@Serializable
data class AuthResponse(
    val token: String
)