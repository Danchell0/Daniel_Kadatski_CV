package com.example.moodchatbot.network

data class GroqMessage(val role: String, val content: String)

data class GroqChatRequest(
    val model: String,
    val messages: List<GroqMessage>,
    val temperature: Float = 0.7f
)

data class GroqChoice(val message: GroqMessage)

data class GroqChatResponse(val choices: List<GroqChoice>)
