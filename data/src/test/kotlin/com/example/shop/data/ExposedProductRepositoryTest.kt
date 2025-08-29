package com.example.shop.data

import com.example.shop.domain.Product
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals

class ExposedProductRepositoryTest {

    private val repository = ExposedProductRepository()

    @BeforeEach
    fun setup() {
        DatabaseFactory.init()
    }

    @AfterEach
    fun teardown() {
    }

    @Test
    fun testCreateAndFindById() = runBlocking {
        val product = Product(0, "Test Product", "Test Description", 10.0)
        val createdProduct = repository.save(product)
        val foundProduct = repository.findById(createdProduct.id)
        assertEquals(createdProduct, foundProduct)
    }

    @Test
    fun testFindAll() = runBlocking {
        val product1 = Product(0, "Test Product 1", "Test Description 1", 10.0)
        val product2 = Product(0, "Test Product 2", "Test Description 2", 20.0)
        repository.save(product1)
        repository.save(product2)
        val allProducts = repository.findAll()
        assertEquals(2, allProducts.size)
    }

    @Test
    fun testUpdate() = runBlocking {
        val product = Product(0, "Test Product", "Test Description", 10.0)
        val createdProduct = repository.save(product)
        val updatedProduct = createdProduct.copy(name = "Updated Product")
        repository.update(updatedProduct)
        val foundProduct = repository.findById(createdProduct.id)
        assertEquals(updatedProduct, foundProduct)
    }

    @Test
    fun testDelete() = runBlocking {
        val product = Product(0, "Test Product", "Test Description", 10.0)
        val createdProduct = repository.save(product)
        repository.delete(createdProduct.id)
        val foundProduct = repository.findById(createdProduct.id)
        assertEquals(null, foundProduct)
    }
}