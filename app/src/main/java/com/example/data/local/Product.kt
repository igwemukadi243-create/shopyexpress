package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room database entity representing a product.
 * Stores item details including name, description, price, city, and category.
 */
@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val city: String = "",
    val category: String = "",
    val imageUrl: String = "",
    val timestamp: Long = System.currentTimeMillis()
) {
    /**
     * Approximate price in Congolese Francs (CDF / FC) based on ~2,800 FC per 1 USD.
     */
    val priceCdf: Long
        get() = (price * 2800).toLong()

    val formattedPriceUsd: String
        get() = String.format(java.util.Locale.US, "%.2f $", price)

    val formattedPriceCdf: String
        get() {
            val formatted = java.text.NumberFormat.getIntegerInstance(java.util.Locale.FRENCH).format(priceCdf)
            return "$formatted FC"
        }
}
