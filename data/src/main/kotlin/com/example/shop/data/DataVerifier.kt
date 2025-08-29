package com.example.shop.data

import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object DataVerifier {
    fun verifySeededData() {
        val repository = ExposedProductRepository()
        runBlocking {
            val totalProducts = transaction { Products.selectAll().count() }
            println("Total products in database: $totalProducts")
            
            if (totalProducts > 0) {
                println("\nFirst 5 products:")
                val firstFive = repository.findAll().take(5)
                firstFive.forEach { product ->
                    println("ID: ${product.id}, Name: ${product.name}, Price: $${product.price}")
                }
                
                println("\nSample product descriptions:")
                firstFive.take(2).forEach { product ->
                    println("${product.name}: ${product.description}")
                    println()
                }
            }
        }
    }
}