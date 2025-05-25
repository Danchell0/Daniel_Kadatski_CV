package com.example.moodchatbot.model

data class ChatMessage(
    val text: String,
    val senderType: Int,
    val timestamp: Long = System.currentTimeMillis()
)
