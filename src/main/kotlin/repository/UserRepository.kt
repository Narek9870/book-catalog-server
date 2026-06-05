package com.example.repository

import com.example.db.DatabaseFactory.dbQuery
import com.example.db.Users
import com.example.models.User
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

// интерфейс(Слой Domain)
interface UserRepository {
    suspend fun findUserByEmail(email: String): User?
    suspend fun createUser(email: String, passwordHash: String): Int
}

// реализация(Слой Data)
class UserRepositoryImpl : UserRepository {
    override suspend fun findUserByEmail(email: String): User? {
        return dbQuery {
            // Ищем строку в БД и сразу превращаем её в чистую модель User
            val row = Users.select { Users.email eq email }.singleOrNull()
            row?.let {
                User(
                    id = it[Users.id],
                    email = it[Users.email],
                    passwordHash = it[Users.passwordHash]
                )
            }
        }
    }

    override suspend fun createUser(email: String, passwordHash: String): Int {
        return dbQuery {
            Users.insert {
                it[Users.email] = email
                it[Users.passwordHash] = passwordHash
            }[Users.id]
        }
    }
}