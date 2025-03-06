package com.example.chatwithgemini.viewmodels

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatwithgemini.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.TextPart
import kotlinx.coroutines.launch

class MainActivityViewModel: ViewModel() {
    private val TAG = javaClass.simpleName
    private val model = GenerativeModel("gemini-2.0-flash", BuildConfig.API_KEY)
    private val chat = model.startChat()
    var historyHolder  = mutableStateListOf<Content>()
    var messageInputValue by mutableStateOf("")
    var generating by mutableStateOf(false)
    var listState: LazyListState? = null
    var speechRecognizerListening by mutableStateOf(false)
    fun sendMessage(message: String) {
        if(message.isBlank()) return
        viewModelScope.launch {
            historyHolder.add(0, Content("user", listOf(TextPart(message))))
            generating = true
            chat.sendMessage(message)
            generating = false
            historyHolder.clear()
            historyHolder.addAll(chat.history.reversed())
        }
    }
}