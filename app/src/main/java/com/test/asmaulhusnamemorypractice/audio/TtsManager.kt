package com.test.asmaulhusnamemorypractice.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

/**
 * On-demand TextToSpeech engine helper for pronouncing Arabic names.
 *
 * Uses TextToSpeech.OnInitListener for proper async initialization.
 * The speak function is guarded so it only fires after SUCCESS init.
 */
class TtsManager(context: Context) : TextToSpeech.OnInitListener {

    companion object {
        private const val TAG = "TtsManager"
    }

    private var tts: TextToSpeech? = null
    private var isReady = false

    init {
        // Create the TTS engine — onInit will be called asynchronously
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // Try Arabic Saudi Arabia first, then generic Arabic
            val locales = listOf(
                Locale.forLanguageTag("ar-SA"),
                Locale.forLanguageTag("ar")
            )

            var langSet = false
            for (locale in locales) {
                val result = tts?.setLanguage(locale) ?: TextToSpeech.LANG_NOT_SUPPORTED
                if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.i(TAG, "TTS language set to: ${locale.toLanguageTag()}")
                    langSet = true
                    break
                }
            }

            if (!langSet) {
                Log.w(TAG, "Arabic TTS not available on this device. Using default locale.")
                tts?.language = Locale.getDefault()
            }

            tts?.setPitch(1.0f)
            tts?.setSpeechRate(0.8f) // Slower for clear Arabic pronunciation
            isReady = true
            Log.i(TAG, "TextToSpeech initialized successfully")
        } else {
            isReady = false
            Log.e(TAG, "TextToSpeech initialization FAILED with status: $status")
        }
    }

    /**
     * Speak Arabic text on-demand. Only fires if TTS engine is ready.
     */
    fun speakArabic(arabicText: String) {
        if (!isReady) {
            Log.w(TAG, "TTS engine not ready yet — ignoring speak request")
            return
        }
        tts?.stop()
        tts?.speak(
            arabicText,
            TextToSpeech.QUEUE_FLUSH,
            null,
            "asmaul_husna_${System.currentTimeMillis()}"
        )
    }

    fun shutdown() {
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (e: Exception) {
            Log.e(TAG, "Error shutting down TTS", e)
        } finally {
            tts = null
            isReady = false
        }
    }
}
