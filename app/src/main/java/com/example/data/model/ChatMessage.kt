package com.example.data.model

data class ChatMessage(
    val id: String,
    val conversationId: String,
    val senderId: String,
    val senderName: String,
    val text: String,
    val timestampMillis: Long = System.currentTimeMillis(),
    val isFromMe: Boolean,
    val linkedOffer: Offer? = null
)

data class Conversation(
    val id: String,
    val productId: String,
    val productTitle: String,
    val productPriceUsd: Double,
    val productImageUrl: String,
    val otherUserId: String,
    val otherUserName: String,
    val otherUserAvatar: String,
    val lastMessage: String,
    val lastMessageTimeMillis: Long,
    val unreadCount: Int = 0,
    val currentOffer: Offer? = null
)
