package com.example.data.model

enum class OfferStatus {
    PENDING,
    ACCEPTED,
    REJECTED,
    COUNTERED
}

data class Offer(
    val id: String,
    val productId: String,
    val productTitle: String,
    val productImageUrl: String,
    val buyerId: String,
    val buyerName: String,
    val sellerId: String,
    val initialPriceUsd: Double,
    val offeredPriceUsd: Double,
    val status: OfferStatus = OfferStatus.PENDING,
    val timestampMillis: Long = System.currentTimeMillis()
)
