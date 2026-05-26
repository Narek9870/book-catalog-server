package com.example.routes

import com.example.models.BookRequest
import com.example.repository.BookRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.bookRoutes(bookRepository: BookRepository) {
    routing {
        authenticate("auth-jwt") {
            route("/books") {

                get {
                    val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("userId").asInt()
                    val userBooks = bookRepository.getAllBooksForUser(userId)
                    call.respond(HttpStatusCode.OK, userBooks)
                }

                post {
                    val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("userId").asInt()
                    val request = call.receive<BookRequest>()

                    val newBookId = bookRepository.addBook(userId, request)
                    call.respond(HttpStatusCode.Created, mapOf("id" to newBookId))
                }

                put("/{id}") {
                    val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("userId").asInt()
                    val bookId = call.parameters["id"]?.toIntOrNull()

                    if (bookId == null) {
                        call.respond(HttpStatusCode.BadRequest, "Неверный ID книги")
                        return@put
                    }

                    val request = call.receive<BookRequest>()
                    val isUpdated = bookRepository.updateBook(userId, bookId, request)

                    if (isUpdated) call.respond(HttpStatusCode.OK, "Книга обновлена")
                    else call.respond(HttpStatusCode.NotFound, "Книга не найдена")
                }

                delete("/{id}") {
                    val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("userId").asInt()
                    val bookId = call.parameters["id"]?.toIntOrNull()

                    if (bookId == null) {
                        call.respond(HttpStatusCode.BadRequest, "Неверный ID книги")
                        return@delete
                    }

                    val isDeleted = bookRepository.deleteBook(userId, bookId)

                    if (isDeleted) call.respond(HttpStatusCode.OK, "Книга удалена")
                    else call.respond(HttpStatusCode.NotFound, "Книга не найдена")
                }
            }
        }
    }
}