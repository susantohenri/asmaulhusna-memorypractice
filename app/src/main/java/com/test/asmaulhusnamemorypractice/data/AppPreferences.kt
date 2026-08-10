package com.test.asmaulhusnamemorypractice.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class AppLanguage {
    AUTO, // Detect from OS
    EN,   // English
    ID    // Bahasa Indonesia
}

enum class AppTheme {
    AUTO,  // System default
    LIGHT, // Light Mode
    DARK   // Dark Mode
}

class AppPreferences(context: Context) {
    private val sharedPrefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)

    private val KEY_LANGUAGE = "language_pref"
    private val KEY_THEME = "theme_pref"

    private val _languageState = MutableStateFlow(
        AppLanguage.valueOf(sharedPrefs.getString(KEY_LANGUAGE, AppLanguage.AUTO.name) ?: AppLanguage.AUTO.name)
    )
    val languageState: StateFlow<AppLanguage> = _languageState.asStateFlow()

    private val _themeState = MutableStateFlow(
        AppTheme.valueOf(sharedPrefs.getString(KEY_THEME, AppTheme.AUTO.name) ?: AppTheme.AUTO.name)
    )
    val themeState: StateFlow<AppTheme> = _themeState.asStateFlow()

    fun setLanguage(language: AppLanguage) {
        sharedPrefs.edit().putString(KEY_LANGUAGE, language.name).apply()
        _languageState.update { language }
    }

    fun setTheme(theme: AppTheme) {
        sharedPrefs.edit().putString(KEY_THEME, theme.name).apply()
        _themeState.update { theme }
    }
}
