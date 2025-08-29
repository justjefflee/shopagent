package com.example.shop.data

import com.example.shop.domain.Product
import com.example.shop.domain.ProductRepository
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SqlExpressionBuilder.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update

class ExposedProductRepository : ProductRepository {

    override fun findById(id: Long): Product? {
        return transaction {
            Products.selectAll().where { Products.id eq id }
                .map { toProduct(it) }
                .firstOrNull()
        }
    }

    override fun findAll(): List<Product> {
        return transaction {
            Products.selectAll()
                .map { toProduct(it) }
        }
    }

    override fun save(product: Product): Product {
        var productId = 0L
        transaction {
            productId = Products.insert {
                it[name] = product.name
                it[description] = product.description
                it[price] = product.price
            } get Products.id
        }
        return product.copy(id = productId)
    }

    override fun update(product: Product): Product {
        transaction {
            Products.update({ Products.id eq product.id }) {
                it[name] = product.name
                it[description] = product.description
                it[price] = product.price
            }
        }
        return product
    }

    override fun delete(id: Long) {
        transaction {
            Products.deleteWhere { Products.id eq id }
        }
    }

    private fun toProduct(row: ResultRow): Product = Product(
        id = row[Products.id],
        name = row[Products.name],
        description = row[Products.description],
        price = row[Products.price]
    )
}
