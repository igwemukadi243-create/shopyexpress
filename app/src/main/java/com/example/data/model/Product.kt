package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class Product(
    @DocumentId val id: String = "",
    val title: String = "",
    val description: String = "",
    val priceUsd: Double = 0.0,
    val originalPriceUsd: Double? = null,
    val brand: String = "",
    val size: String = "",
    val categoryId: String = "",
    val condition: String = "Très bon état",
    val city: String = "Kinshasa",
    val commune: String = "Gombe",
    val imageUrl: String = "",
    val sellerId: String = "",
    val sellerName: String = "",
    val sellerAvatarUrl: String = "",
    val sellerRating: Double = 5.0,
    val sellerSalesCount: Int = 0,
    val isVerifiedSeller: Boolean = true,
    val isFavorite: Boolean = false,
    val isSold: Boolean = false,
    val viewsCount: Int = 1,
    val likesCount: Int = 0,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    // In DRC, 1 USD is approximately 2,800 CDF (Franc Congolais)
    val priceCdf: Long
        get() = (priceUsd * 2800).toLong()

    val formattedPriceCdf: String
        get() {
            val formatted = java.text.NumberFormat.getIntegerInstance(java.util.Locale.FRENCH).format(priceCdf)
            return "$formatted FC"
        }

    val formattedPriceUsd: String
        get() = String.format(java.util.Locale.US, "%.0f $", priceUsd)

    val createdAtMillis: Long
        get() = createdAt?.toDate()?.time ?: System.currentTimeMillis()
}
