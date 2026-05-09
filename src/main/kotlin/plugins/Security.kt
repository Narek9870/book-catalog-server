package com.example.plugins

import com.auth0.jwt.JWT
import com.example.utils.JwtConfig
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Application.configureSecurity() {
    install(Authentication) {
        jwt("auth-jwt") { // Название нашей защиты
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