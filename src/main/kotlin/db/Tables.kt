package com.example.db

import org.jetbrains.exposed.sql.Table

// Таблица пользователей
object Users : Table("users") {
    val id = integer("id").autoIncrement()
    val email = varchar("email", 128).uniqueIndex()
    val passwordHash = varchar("password_hash", 128)

    override val primaryKey = PrimaryKey(id)
}

// Таблица книг
object Books : Table("books") {
    val id = integer("id").autoIncrement()
    val title = varchar("title", 256)
    val author = varchar("author", 256)
    val genre = varchar("genre", 100).nullable() // может быть пустым
    val rating = integer("rating")
    val review = text("review").nullable() // может быть пустым

    // Привязываем книгу к конкретному пользователю
    val userId = integer("user_id").references(Users.id)

    override val primaryKey = PrimaryKey(id)
}