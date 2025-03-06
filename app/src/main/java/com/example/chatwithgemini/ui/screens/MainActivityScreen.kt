package com.example.chatwithgemini.ui.screens

import android.content.Context
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.example.chatwithgemini.R
import com.example.chatwithgemini.ui.theme.ChatWithGeminiTheme
import com.example.chatwithgemini.utilities.TextFormatter
import com.example.chatwithgemini.viewmodels.MainActivityViewModel
import com.google.ai.client.generativeai.type.asTextOrNull

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainActivityScreen(
    context: Context,
    viewModel: MainActivityViewModel,
    speechRecognizerLauncher: ActivityResultLauncher<Unit?>,
    modifier: Modifier = Modifier,
) {
    val historyHolder = viewModel.historyHolder
    viewModel.listState = rememberLazyListState()
    var darkMode by remember { mutableStateOf(true) }
    ChatWithGeminiTheme(
        darkTheme = darkMode
    ) {
//        darkMode = isSystemInDarkTheme()
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    { Text("Gemini") },
                    actions = {
                        IconToggleButton(
                            isSystemInDarkTheme(),
                            {
                                darkMode = !darkMode
                            }
                        ) {
                            Icon(
                                context.getDrawable(if (darkMode) R.drawable.baseline_dark_mode_24 else R.drawable.baseline_light_mode_24)
                                    ?.toBitmap()
                                    ?.asImageBitmap()!!,
                                ""
                            )
                        }
                    }
                )
            },
            bottomBar = {
                TextField(
                    value = viewModel.messageInputValue,
                    onValueChange = { viewModel.messageInputValue = it },
                    leadingIcon = {
                        Icon(
                            context.getDrawable(R.drawable.baseline_mic_none_24)?.toBitmap()
                                ?.asImageBitmap()!!,
                            "speak",
                            Modifier.clickable {
                                speechRecognizerLauncher.launch(null)
                            }
                        )
                    },
                    trailingIcon = {
                        Icon(
                            Icons.Default.Send,
                            "search",
                            Modifier.clickable(onClick = { sendMessage(viewModel) })
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = { sendMessage(viewModel) }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .imePadding(),
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .navigationBarsPadding()
                    .then(modifier),
                verticalArrangement = Arrangement.Bottom,
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    state = viewModel.listState ?: rememberLazyListState(),
                    reverseLayout = true
                ) {
                    items(historyHolder.size) {
                        val content = historyHolder[it]
                        val role = content.role
                        val parts = content.parts
                        for (part in parts) {
                            MessageBubble(
                                part.asTextOrNull() ?: "",
                                role ?: "model",
                            )
                        }
                    }
                }
                when {
                    viewModel.generating -> {
                        MessageBubble("...", "model")
                    }
                }
            }
        }
    }
}

@Composable
fun MessageBubble(
    message: String,
    role: String,
    modifier: Modifier = Modifier
) {
    val bgColor = if (role == "user") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer
    val textColor = if (role == "user") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer
    val alignment = if (role == "user") Alignment.End else Alignment.Start
    Column(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .then(modifier)
    ) {
        Box(
            modifier = Modifier
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = bgColor,
                    )
                .align(alignment)
                .padding(12.dp)
                .widthIn(min = 0.dp, max = 256.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = TextFormatter.getBoldSpannableText(message),
                color = textColor,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

fun sendMessage(viewModel: MainActivityViewModel) {
    if(viewModel.generating) {
        return
    }
    viewModel.sendMessage(viewModel.messageInputValue)
    viewModel.messageInputValue = ""
}