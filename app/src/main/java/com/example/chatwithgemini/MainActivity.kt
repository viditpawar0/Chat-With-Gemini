package com.example.chatwithgemini

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContract
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chatwithgemini.ui.screens.MainActivityScreen
import com.example.chatwithgemini.viewmodels.MainActivityViewModel
import com.google.ai.client.generativeai.type.UnknownException

class MainActivity : ComponentActivity() {
    private val TAG: String? = javaClass.simpleName
    private lateinit var viewModel: MainActivityViewModel
    val speechRecognizerLauncher = registerForActivityResult(
        object : ActivityResultContract<Unit?, String>() {
            override fun createIntent(context: Context, input: Unit?): Intent {
                return Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                    .apply {
                        putExtra(
                            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                        )
                    }
            }

            override fun parseResult(resultCode: Int, intent: Intent?): String {
                if (resultCode != RESULT_OK) throw UnknownException("Error processing speech")
                return intent?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0) ?: throw Exception("No speech detected")
            }

        }
    ) {
        viewModel.sendMessage(it)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 0)
        setContent {
            viewModel = viewModel<MainActivityViewModel>()
            MainActivityScreen(this, viewModel, speechRecognizerLauncher)
        }
    }
}