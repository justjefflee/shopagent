package com.example.shop.data

import com.arakelian.faker.service.RandomAddress
import com.arakelian.faker.service.RandomPerson
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.text.DecimalFormat
import kotlin.random.Random

object DatabaseSeeder {
    private val priceFormatter = DecimalFormat("#.##")
    
    private val poolEquipmentCategories = listOf(
        "Pool Pumps" to listOf("Variable Speed Pool Pump", "Single Speed Pool Pump", "Dual Speed Pool Pump", "Above Ground Pool Pump"),
        "Pool Filters" to listOf("Sand Filter", "Cartridge Filter", "DE Filter", "Pool Filter Cartridge"),
        "Pool Heaters" to listOf("Gas Pool Heater", "Electric Pool Heater", "Heat Pump", "Solar Pool Heater"),
        "Pool Cleaners" to listOf("Robotic Pool Cleaner", "Suction Pool Cleaner", "Pressure Pool Cleaner", "Manual Pool Vacuum"),
        "Pool Chemicals" to listOf("Chlorine Tablets", "Pool Shock", "pH Increaser", "pH Decreaser", "Algaecide", "Pool Clarifier"),
        "Pool Accessories" to listOf("Pool Skimmer", "Pool Brush", "Pool Net", "Pool Cover", "Pool Steps", "Pool Ladder"),
        "Automation" to listOf("Pool Control System", "Pool Timer", "Salt Water Generator", "UV Sanitizer"),
        "Pool Lighting" to listOf("LED Pool Light", "Halogen Pool Light", "Fiber Optic Pool Light", "Floating Pool Light"),
        "Water Features" to listOf("Pool Waterfall", "Pool Fountain", "Pool Jets", "Spillway"),
        "Pool Maintenance" to listOf("Pool Test Kit", "Pool Thermometer", "Pool Tile Cleaner", "Pool Vacuum Hose")
    )
    
    private val poolBrands = listOf("Pentair", "Hayward", "Jandy", "Polaris", "Dolphin", "Kreepy Krauly", "Aquabot", "Sta-Rite", "Intex", "Bestway")
    
    fun seedDatabase(count: Int = 1000) {
        transaction {
            // Check if products already exist
            val existingCount = Products.selectAll().count()
            if (existingCount > 0) {
                println("Database already contains $existingCount products. Skipping seeding.")
                return@transaction
            }
            
            println("Seeding database with $count pool equipment products...")
            
            repeat(count) { index ->
                val (category, productTypes) = poolEquipmentCategories.random()
                val productType = productTypes.random()
                val brand = poolBrands.random()
                
                val name = generateProductName(brand, productType, category)
                val description = generateProductDescription(productType, brand, category)
                val price = generatePrice(category, productType)
                
                Products.insert {
                    it[Products.name] = name
                    it[Products.description] = description
                    it[Products.price] = price
                }
                
                if ((index + 1) % 100 == 0) {
                    println("Inserted ${index + 1} products...")
                }
            }
            
            println("Successfully seeded database with $count pool equipment products!")
        }
    }
    
    private fun generateProductName(brand: String, productType: String, category: String): String {
        val modelNumber = when (category) {
            "Pool Pumps" -> "P${Random.nextInt(100, 999)}"
            "Pool Filters" -> "F${Random.nextInt(100, 999)}"
            "Pool Heaters" -> "H${Random.nextInt(100, 999)}"
            "Pool Cleaners" -> "C${Random.nextInt(100, 999)}"
            else -> "${category.first()}${Random.nextInt(100, 999)}"
        }
        
        return "$brand $productType $modelNumber"
    }
    
    private fun generateProductDescription(productType: String, brand: String, category: String): String {
        val baseDescription = when (category) {
            "Pool Pumps" -> "High-efficiency pool pump designed for optimal circulation and energy savings. Features variable speed technology for reduced operating costs."
            "Pool Filters" -> "Advanced filtration system that removes debris and contaminants from pool water. Easy maintenance and long-lasting performance."
            "Pool Heaters" -> "Reliable pool heating solution for extended swimming seasons. Energy-efficient design with precise temperature control."
            "Pool Cleaners" -> "Automated cleaning system that removes dirt, leaves, and debris from pool surfaces. Low maintenance operation."
            "Pool Chemicals" -> "Professional-grade pool chemical for maintaining proper water balance and sanitation. Safe and effective formulation."
            "Pool Accessories" -> "Essential pool accessory for maintenance and safety. Durable construction built to last."
            "Automation" -> "Smart pool control system for automated maintenance and monitoring. Easy-to-use interface with advanced features."
            "Pool Lighting" -> "Energy-efficient pool lighting solution for enhanced ambiance and safety. Long-lasting LED technology."
            "Water Features" -> "Decorative water feature that adds elegance and tranquility to your pool area. Weather-resistant materials."
            "Pool Maintenance" -> "Professional pool maintenance tool for keeping your pool in perfect condition. Precision-engineered for accuracy."
            else -> "Quality pool equipment from $brand. Professional-grade construction for reliability and performance."
        }
        
        val features = listOf(
            "Corrosion-resistant materials",
            "Easy installation",
            "Energy efficient design",
            "Weather resistant",
            "Low maintenance",
            "Professional quality",
            "Durable construction",
            "Optimal performance",
            "User-friendly operation",
            "Industry standard compatibility"
        ).shuffled().take(2)
        
        return "$baseDescription ${features.joinToString(". ")}. Manufactured by $brand with industry-leading warranty."
    }
    
    private fun generatePrice(category: String, productType: String): Double {
        val basePrice = when (category) {
            "Pool Pumps" -> Random.nextDouble(299.99, 1299.99)
            "Pool Filters" -> Random.nextDouble(149.99, 899.99)
            "Pool Heaters" -> Random.nextDouble(899.99, 4999.99)
            "Pool Cleaners" -> Random.nextDouble(199.99, 2499.99)
            "Pool Chemicals" -> Random.nextDouble(9.99, 89.99)
            "Pool Accessories" -> Random.nextDouble(19.99, 299.99)
            "Automation" -> Random.nextDouble(399.99, 1999.99)
            "Pool Lighting" -> Random.nextDouble(49.99, 399.99)
            "Water Features" -> Random.nextDouble(199.99, 1599.99)
            "Pool Maintenance" -> Random.nextDouble(12.99, 149.99)
            else -> Random.nextDouble(29.99, 499.99)
        }
        
        return priceFormatter.format(basePrice).toDouble()
    }
}