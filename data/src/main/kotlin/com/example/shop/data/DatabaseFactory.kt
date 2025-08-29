package com.example.shop.data

import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object DatabaseFactory {
    fun init(seedData: Boolean = true) {
        val driverClassName = "org.h2.Driver"
        val jdbcURL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;"
        val database = Database.connect(jdbcURL, driverClassName)
        transaction(database) {
            SchemaUtils.create(Products)
        }
        
        // Seed the database with pool equipment data (skip during tests)
        if (seedData) {
            DatabaseSeeder.seedDatabase(1000)
        }
    }
}
