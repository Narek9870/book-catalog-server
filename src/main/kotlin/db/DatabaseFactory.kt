package com.example.db

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {
        val driverClassName = "org.postgresql.Driver"
        //ВАРИАНТ 1: УДАЛЕННАЯ БАЗА NEON.TECH
        val jdbcURL = "jdbc:postgresql://ep-noisy-term-al8tjwma-pooler.c-3.eu-central-1.aws.neon.tech/neondb?sslmode=require"
        val user = "neondb_owner"
        val password = "npg_wn6ihLVYfS9z"

        //ВАРИАНТ 2: ЛОКАЛЬНАЯ БАЗА (ДЛЯ САМСУНГА)
//        val jdbcURL = "jdbc:postgresql://localhost:5433/book_catalog_db"
//        val user = "postgres"
//        val password = "12345"

        // Подключаемся к БД
        val database = Database.connect(jdbcURL, driverClassName, user, password)

        // Создаем таблицы, если их еще нет
        transaction(database) {
            SchemaUtils.create(Users, Books)
        }
    }

    // Вспомогательная функция для выполнения запросов к БД в фоне
    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}