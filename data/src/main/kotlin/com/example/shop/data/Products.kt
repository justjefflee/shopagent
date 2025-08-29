package com.example.shop.data

import org.jetbrains.exposed.v1.core.Table

object Products : Table() {
    val id = long("id").autoIncrement()
    val name = varchar("name", 255)
    val description = varchar("description", 1024)
    val price = double("price")

    override val primaryKey = PrimaryKey(id)
}
