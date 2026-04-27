package com.example.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.Date

object JwtConfig {
    // Секретный ключ (пароль для генерации токенов). В реальных проектах его прячут, но нам пойдет и так.
    private const val secret = "my-super-secret-key-for-book-catalog"
    private const val issuer = "book-catalog-server"
    private const val validityInMs = 36_000_00 * 24 // Токен живет 24 часа

    val algorithm = Algorithm.HMAC512(secret)

    // Функция, которая делает сам токен
    fun generateToken(userId: Int): String = JWT.create()
        .withIssuer(issuer)
        .withClaim("userId", userId) // Зашиваем ID юзера прямо в токен
        .withExpiresAt(Date(System.currentTimeMillis() + validityInMs))
        .sign(algorithm)
}