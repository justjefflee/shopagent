package com.example.shop.domain

interface ProductRepository {
    fun findById(id: Long): Product?
    fun findAll(): List<Product>
    fun save(product: Product): Product
    fun update(product: Product): Product
    fun delete(id: Long)
}
