package com.example.moodchatbot.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moodchatbot.ChatMessageAdapter
import com.example.moodchatbot.model.ChatMessage
import com.example.moodchatbot.network.GroqChatRequest
import com.example.moodchatbot.network.GroqMessage
import com.example.moodchatbot.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    // IMPORTANT: Remind user to replace this
    private const val GROQ_API_KEY = "YOUR_GROQ_API_KEY_PLACEHOLDER"

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages

    private val _selectedMood = MutableStateFlow<String>("Neutral") // Default mood
    val selectedMood: StateFlow<String> = _selectedMood

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState

    fun setMood(mood: String) {
        _selectedMood.value = mood
    }

    fun sendMessage(userInput: String) {
        if (userInput.isBlank()) return

        // 1. Add user message to UI
        val userMessage = ChatMessage(userInput, ChatMessageAdapter.VIEW_TYPE_USER_MESSAGE)
        _chatMessages.value = _chatMessages.value + userMessage

        viewModelScope.launch {
            try {
                // 2. Construct system message based on mood
                val systemPrompt = when (_selectedMood.value) {
                    "Flirty" -> "You are a flirty chatbot. Respond in a charming and playful manner."
                    "Aggressive" -> "You are an aggressive chatbot. Your responses should be confrontational and assertive."
                    "Funny" -> "You are a funny chatbot. Try to make the user laugh with witty and humorous replies."
                    else -> "You are a helpful assistant." // Neutral or default
                }

                val messagesForApi = mutableListOf<GroqMessage>()
                messagesForApi.add(GroqMessage("system", systemPrompt))
                // Optional: Add last few messages from _chatMessages.value for context
                // For simplicity, just sending the current user input and system prompt
                messagesForApi.add(GroqMessage("user", userInput))

                // 3. Create request for Groq API
                val request = GroqChatRequest(
                    model = "llama3-8b-8192", // Or the chosen model
                    messages = messagesForApi
                    // temperature can be adjusted if needed
                )

                // 4. Call API
                val apiKey = "Bearer $GROQ_API_KEY"
                val response = RetrofitClient.instance.getChatCompletion(apiKey, request)

                if (response.isSuccessful && response.body() != null) {
                    val botReplyText = response.body()!!.choices.firstOrNull()?.message?.content ?: "Sorry, I couldn't get a response."
                    val botMessage = ChatMessage(botReplyText, ChatMessageAdapter.VIEW_TYPE_BOT_MESSAGE)
                    _chatMessages.value = _chatMessages.value + botMessage
                    _errorState.value = null // Clear any previous error
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown API error"
                    _errorState.value = "API Error: ${response.code()} - $errorBody"
                    val botErrorMessage = ChatMessage("Error: Could not get response. $errorBody", ChatMessageAdapter.VIEW_TYPE_BOT_MESSAGE)
                    _chatMessages.value = _chatMessages.value + botErrorMessage
                }
            } catch (e: Exception) {
                _errorState.value = "Network Error: ${e.message}"
                val botExceptionMessage = ChatMessage("Error: Network issue - ${e.message}", ChatMessageAdapter.VIEW_TYPE_BOT_MESSAGE)
                _chatMessages.value = _chatMessages.value + botExceptionMessage
            }
        }
    }
}
