package org.example.app

import com.example.shop.data.DatabaseFactory
import com.example.shop.data.DataVerifier

fun main() {
    DatabaseFactory.init()
    DataVerifier.verifySeededData()
}