package com.test.asmaulhusnamemorypractice.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

/**
 * On-demand TextToSpeech engine helper for pronouncing Arabic names.
 */
class TtsManager(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    private var isInitialized = false

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val arabicLocale = Locale.forLanguageTag("ar")
            val result = tts?.setLanguage(arabicLocale)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.w("TtsManager", "Arabic language data missing or not supported on this device. Trying default locale.")
                tts?.language = Locale.getDefault()
            } else {
                tts?.setPitch(1.0f)
                tts?.setSpeechRate(0.85f) // Slightly slower rate for clear Arabic pronunciation
            }
            isInitialized = true
        } else {
            Log.e("TtsManager", "TextToSpeech initialization failed with status $status")
        }
    }

    fun speakArabic(arabicText: String) {
        if (isInitialized && tts != null) {
            tts?.stop()
            tts?.speak(arabicText, TextToSpeech.QUEUE_FLUSH, null, "asmaul_husna_tts_${System.currentTimeMillis()}")
        } else {
            Log.w("TtsManager", "TextToSpeech engine not ready yet")
        }
    }

    fun shutdown() {
        try {
            tts?.stop()
            tts?.shutdown()
            tts = null
            isInitialized = false
        } catch (e: Exception) {
            Log.e("TtsManager", "Error shutting down TTS", e)
        }
    }
}
