package com.example.moodchatbot.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface GroqApiService {
    @POST("openai/v1/chat/completions") // Endpoint
    suspend fun getChatCompletion(
        @Header("Authorization") apiKey: String, // API Key as Bearer token
        @Body request: GroqChatRequest     // Request body
    ): Response<GroqChatResponse>          // Retrofit Response wrapper for details
}
