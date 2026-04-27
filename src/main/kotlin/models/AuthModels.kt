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
// Это Android присылает нам, когда хочет добавить книгу
@Serializable
data class BookRequest(
    val title: String,
    val author: String,
    val genre: String? = null,
    val rating: Int,
    val review: String? = null
)

// Это Сервер отправляет в Android, когда тот просит список книг
@Serializable
data class BookResponse(
    val id: Int,
    val title: String,
    val author: String,
    val genre: String?,
    val rating: Int,
    val review: String?
)