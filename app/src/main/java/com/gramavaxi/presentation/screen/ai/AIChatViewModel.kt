package com.gramavaxi.presentation.screen.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gramavaxi.domain.model.ChatMessage
import com.gramavaxi.domain.repository.AIRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AIChatViewModel @Inject constructor(
    private val aiRepository: AIRepository
) : ViewModel() {

    private val _messages = MutableStateFlow(
        listOf(
            ChatMessage(
                id = UUID.randomUUID().toString(),
                content = "Hello! I am Grama-Vaxi AI. Ask me anything about livestock health, vaccination, nutrition, or when to call a vet.",
                isFromUser = false,
                timestamp = System.currentTimeMillis()
            )
        )
    )
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        val userMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            content = text,
            isFromUser = true,
            timestamp = System.currentTimeMillis()
        )

        val loadingMessage = ChatMessage(
            id = "loading",
            content = "...",
            isFromUser = false,
            timestamp = System.currentTimeMillis(),
            isLoading = true
        )

        _messages.value = _messages.value + userMessage + loadingMessage
        _isLoading.value = true

        viewModelScope.launch {
            val result = aiRepository.sendChatMessage(text)
            val response = result.getOrElse {
                userFriendlyError(it)
            }

            val aiMessage = ChatMessage(
                id = UUID.randomUUID().toString(),
                content = response,
                isFromUser = false,
                timestamp = System.currentTimeMillis()
            )

            _messages.value = _messages.value.filter { it.id != "loading" } + aiMessage
            _isLoading.value = false
        }
    }

    fun clearChat() {
        _messages.value = listOf(
            ChatMessage(
                id = UUID.randomUUID().toString(),
                content = "Chat cleared. How can I help you?",
                isFromUser = false,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    private fun userFriendlyError(error: Throwable): String {
        val message = error.localizedMessage.orEmpty()
        return when {
            "429" in message || "quota" in message.lowercase() || "too many" in message.lowercase() ->
                "The AI service is temporarily over its usage limit. Please try again after some time."
            "API key" in message || "403" in message || "401" in message ->
                "The AI service is not configured correctly. Please check the Gemini API key."
            else ->
                "Sorry, I am having trouble connecting. Please check your internet connection and try again."
        }
    }
}
