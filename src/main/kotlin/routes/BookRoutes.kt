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
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.bookRoutes() {
    routing {
        authenticate("auth-jwt") {
            route("/books") {

                get {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal!!.payload.getClaim("userId").asInt()

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

                post {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal!!.payload.getClaim("userId").asInt()
                    val request = call.receive<BookRequest>()

                    val newBookId = transaction {
                        Books.insert {
                            it[title] = request.title
                            it[author] = request.author
                            it[genre] = request.genre
                            it[rating] = request.rating
                            it[review] = request.review
                            it[Books.userId] = userId
                        }[Books.id]
                    }
                    call.respond(HttpStatusCode.Created, mapOf("id" to newBookId))
                }

                // НОВОЕ: ОБНОВЛЕНИЕ КНИГИ (PUT)
                put("/{id}") {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal!!.payload.getClaim("userId").asInt()
                    val bookId = call.parameters["id"]?.toIntOrNull()

                    if (bookId == null) {
                        call.respond(HttpStatusCode.BadRequest, "Неверный ID книги")
                        return@put
                    }

                    val request = call.receive<BookRequest>()

                    val updatedRows = transaction {
                        // Обновляем только если совпадает id книги и id пользователя
                        Books.update({ (Books.id eq bookId) and (Books.userId eq userId) }) {
                            it[title] = request.title
                            it[author] = request.author
                            it[genre] = request.genre
                            it[rating] = request.rating
                            it[review] = request.review
                        }
                    }

                    if (updatedRows > 0) {
                        call.respond(HttpStatusCode.OK, "Книга обновлена")
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Книга не найдена")
                    }
                }

                delete("/{id}") {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal!!.payload.getClaim("userId").asInt()
                    val bookId = call.parameters["id"]?.toIntOrNull()

                    if (bookId == null) {
                        call.respond(HttpStatusCode.BadRequest, "Неверный ID книги")
                        return@delete
                    }

                    val deletedRows = transaction {
                        Books.deleteWhere { (Books.id eq bookId) and (Books.userId eq userId) }
                    }

                    if (deletedRows > 0) {
                        call.respond(HttpStatusCode.OK, "Книга удалена")
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Книга не найдена")
                    }
                }
            }
        }
    }
}