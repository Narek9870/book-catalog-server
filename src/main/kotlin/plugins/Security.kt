package com.example.plugins // или просто com.example, смотря где создашь

import com.auth0.jwt.JWT
import com.example.utils.JwtConfig
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Application.configureSecurity() {
    install(Authentication) {
        jwt("auth-jwt") { // Название нашей стратегии защиты
            realm = "Book Catalog Server"
            verifier(
                JWT.require(JwtConfig.algorithm)
                    .withIssuer("book-catalog-server")
                    .build()
            )
            validate { credential ->
                // Проверяем, есть ли в токене userId. Если есть - пропускаем.
                if (credential.payload.getClaim("userId").asInt() != null) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }
}