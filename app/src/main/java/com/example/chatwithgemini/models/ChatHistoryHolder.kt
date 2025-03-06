package com.example.chatwithgemini.models

import com.google.ai.client.generativeai.type.Content

data class ChatHistoryHolder(val history: MutableList<Content>)
