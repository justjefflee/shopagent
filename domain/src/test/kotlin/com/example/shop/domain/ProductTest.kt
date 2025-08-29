package com.example.shop.domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ProductTest {

    @Test
    fun testProductProperties() {
        val product = Product(1, "Test Product", "Test Description", 10.0)
        assertEquals(1, product.id)
        assertEquals("Test Product", product.name)
        assertEquals("Test Description", product.description)
        assertEquals(10.0, product.price)
    }
}
