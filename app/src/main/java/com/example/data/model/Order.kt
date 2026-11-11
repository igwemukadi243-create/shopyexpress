package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

enum class OrderStatus(val labelFr: String) {
    PENDING("En attente de validation"),
    PROCESSING("En préparation chez le vendeur"),
    SHIPPED("En transit d'expédition"),
    DELIVERED("Livré & Conforme"),
    CANCELLED("Annulé")
}

/**
 * Modes de paiement disponibles sur ShopyExpress.
 * Les paiements mobiles et cartes sont agrégés et sécurisés par la passerelle iKeePay RDC.
 */
enum class PaymentMethod(
    val title: String,
    val subtitle: String,
    val provider: String = "Mobile Money RDC"
) {
    MPESA(
        "Vodacom M-Pesa",
        "Paiement direct sécurisé par compte M-Pesa RDC (FlexPay)",
        "Vodacom RDC"
    ),
    AIRTEL_MONEY(
        "Airtel Money",
        "Paiement instantané via compte Airtel RDC (FlexPay)",
        "Airtel RDC"
    ),
    ORANGE_MONEY(
        "Orange Money",
        "Portefeuille électronique Orange RDC (FlexPay)",
        "Orange RDC"
    ),
    CARD(
        "Carte Bancaire (Visa / Mastercard)",
        "Paiement en ligne sécurisé en USD ou CDF",
        "Passerelle RDC"
    ),
    CASH_ON_DELIVERY(
        "Paiement à la livraison",
        "Remise en main propre ou règlement au livreur",
        "Local"
    );

    val isIkeePay: Boolean
        get() = provider == "iKeePay"
}

@IgnoreExtraProperties
data class Order(
    @DocumentId val id: String = "",
    val buyerId: String = "",
    val sellerId: String = "",
    val productId: String = "",
    val productTitle: String = "",
    val productPriceUsd: Double = 0.0,
    val buyerProtectionFeeUsd: Double = 1.0,
    val deliveryFeeUsd: Double = 2.0,
    val totalUsd: Double = 0.0,
    val productImageUrl: String = "",
    val sellerName: String = "",
    val buyerCity: String = "Kinshasa",
    val deliveryAddress: String = "",
    val phoneNumber: String = "",
    val paymentMethod: PaymentMethod = PaymentMethod.MPESA,
    val paymentGateway: String = "iKeePay",
    val ikeepayTransactionId: String = "",
    val status: OrderStatus = OrderStatus.PROCESSING,
    val trackingCode: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
) {
    val createdAtMillis: Long
        get() = createdAt?.toDate()?.time ?: System.currentTimeMillis()
}
