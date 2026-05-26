package com.example.repository

import com.example.db.DatabaseFactory.dbQuery
import com.example.db.Users
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

class UserRepository {
    suspend fun findUserByEmail(email: String): ResultRow? {
        return dbQuery {
            Users.select { Users.email eq email }.singleOrNull()
        }
    }

    suspend fun createUser(email: String, passwordHash: String): Int {
        return dbQuery {
            Users.insert {
                it[Users.email] = email
                it[Users.passwordHash] = passwordHash
            }[Users.id]
        }
    }
}