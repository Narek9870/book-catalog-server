package com.example.service

import com.example.db.Users
import com.example.models.UserCredentials
import com.example.repository.UserRepository
import com.example.utils.JwtConfig
import org.mindrot.jbcrypt.BCrypt

class AuthService(private val userRepository: UserRepository) {

    suspend fun register(credentials: UserCredentials): Result<String> {
        val existingUser = userRepository.findUserByEmail(credentials.email)
        if (existingUser != null) {
            return Result.failure(Exception("Пользователь уже существует"))
        }

        val hashedPassword = BCrypt.hashpw(credentials.password, BCrypt.gensalt())
        val newUserId = userRepository.createUser(credentials.email, hashedPassword)
        val token = JwtConfig.generateToken(newUserId)

        return Result.success(token)
    }

    suspend fun login(credentials: UserCredentials): Result<String> {
        val user = userRepository.findUserByEmail(credentials.email)
            ?: return Result.failure(Exception("Неверный email или пароль"))

        val passwordMatch = BCrypt.checkpw(credentials.password, user[Users.passwordHash])
        if (!passwordMatch) {
            return Result.failure(Exception("Неверный email или пароль"))
        }

        val token = JwtConfig.generateToken(user[Users.id])
        return Result.success(token)
    }
}