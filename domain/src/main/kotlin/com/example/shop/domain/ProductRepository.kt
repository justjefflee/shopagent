package com.example.shop.domain

interface ProductRepository {
    suspend fun findById(id: Long): Product?
    suspend fun findAll(): List<Product>
    suspend fun save(product: Product): Product
    suspend fun update(product: Product): Product
    suspend fun delete(id: Long)
}
