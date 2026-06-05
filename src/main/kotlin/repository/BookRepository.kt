package com.example.repository

import com.example.db.Books
import com.example.db.DatabaseFactory.dbQuery
import com.example.models.BookRequest
import com.example.models.BookResponse
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

// интерфейс(Слой Domain)
interface BookRepository {
    suspend fun getAllBooksForUser(userId: Int): List<BookResponse>
    suspend fun addBook(userId: Int, request: BookRequest): Int
    suspend fun updateBook(userId: Int, bookId: Int, request: BookRequest): Boolean
    suspend fun deleteBook(userId: Int, bookId: Int): Boolean
}

// реализация(Слой Data)
class BookRepositoryImpl : BookRepository {
    override suspend fun getAllBooksForUser(userId: Int): List<BookResponse> {
        return dbQuery {
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
    }

    override suspend fun addBook(userId: Int, request: BookRequest): Int {
        return dbQuery {
            Books.insert {
                it[title] = request.title
                it[author] = request.author
                it[genre] = request.genre
                it[rating] = request.rating
                it[review] = request.review
                it[Books.userId] = userId
            }[Books.id]
        }
    }

    override suspend fun updateBook(userId: Int, bookId: Int, request: BookRequest): Boolean {
        return dbQuery {
            val updatedRows = Books.update({ (Books.id eq bookId) and (Books.userId eq userId) }) {
                it[title] = request.title
                it[author] = request.author
                it[genre] = request.genre
                it[rating] = request.rating
                it[review] = request.review
            }
            updatedRows > 0
        }
    }

    override suspend fun deleteBook(userId: Int, bookId: Int): Boolean {
        return dbQuery {
            val deletedRows = Books.deleteWhere { (Books.id eq bookId) and (Books.userId eq userId) }
            deletedRows > 0
        }
    }
}