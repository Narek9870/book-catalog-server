package com.example.routes

import com.example.db.Books
import com.example.models.BookRequest
import com.example.models.BookResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.bookRoutes() {
    routing {
        // Защищаем пути токеном
        authenticate("auth-jwt") {
            route("/books") {

                // 1. ПОЛУЧИТЬ ВСЕ КНИГИ (GET /books)
                get {
                    // Достаем userId из токена
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal!!.payload.getClaim("userId").asInt()

                    // Ищем в БД книги ТОЛЬКО этого юзера
                    val userBooks = transaction {
                        Books.select { Books.userId eq userId }.map {
                            BookResponse(
                                id = it[Books.id],
                                title = it[Books.title],
                                author = it[Books.author],
                                genre = it[Books.genre],
                                rating = it[Books.rating],
                                review = it[Books.review]
                            )
                        }
                    }
                    call.respond(HttpStatusCode.OK, userBooks)
                }

                // 2. ДОБАВИТЬ КНИГУ (POST /books)
                post {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal!!.payload.getClaim("userId").asInt()
                    val request = call.receive<BookRequest>()

                    // Сохраняем книгу в БД
                    val newBookId = transaction {
                        Books.insert {
                            it[title] = request.title
                            it[author] = request.author
                            it[genre] = request.genre
                            it[rating] = request.rating
                            it[review] = request.review
                            it[Books.userId] = userId // Привязываем к текущему юзеру!
                        }[Books.id]
                    }

                    // Отвечаем, что всё ок (и отдаем id новой книги)
                    call.respond(HttpStatusCode.Created, mapOf("id" to newBookId))
                }
            }
        }
    }
}