package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class UserProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val name: String = "Utilisateur ShopyExpress",
    val handle: String = "@shopy_user",
    val email: String = "",
    val phone: String = "+243 82 000 0000",
    val city: String = "Kinshasa",
    val commune: String = "Gombe",
    val avatarUrl: String = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=400&q=80",
    val rating: Double = 5.0,
    val reviewsCount: Int = 1,
    val walletBalanceUsd: Double = 0.0,
    val memberSinceYear: Int = 2024,
    val isVerified: Boolean = true,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)
