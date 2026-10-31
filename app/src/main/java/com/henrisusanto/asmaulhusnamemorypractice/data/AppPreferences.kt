package com.henrisusanto.asmaulhusnamemorypractice.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

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

class AppPreferences(private val context: Context) {

    private val KEY_LANGUAGE = stringPreferencesKey("language_pref")
    private val KEY_THEME = stringPreferencesKey("theme_pref")
    private val KEY_REMINDER_ENABLED = booleanPreferencesKey("reminder_enabled")
    private val KEY_REMINDER_HOUR = intPreferencesKey("reminder_hour")
    private val KEY_REMINDER_MINUTE = intPreferencesKey("reminder_minute")
    private val KEY_LAST_INDEX = intPreferencesKey("last_practiced_index")
    private val KEY_USER_NAME = stringPreferencesKey("user_name_pref")

    val languageFlow: Flow<AppLanguage> = context.dataStore.data.map { prefs ->
        val langName = prefs[KEY_LANGUAGE] ?: AppLanguage.AUTO.name
        try { AppLanguage.valueOf(langName) } catch (e: Exception) { AppLanguage.AUTO }
    }

    val themeFlow: Flow<AppTheme> = context.dataStore.data.map { prefs ->
        val themeName = prefs[KEY_THEME] ?: AppTheme.AUTO.name
        try { AppTheme.valueOf(themeName) } catch (e: Exception) { AppTheme.AUTO }
    }

    val reminderEnabledFlow: Flow<Boolean> = context.dataStore.data.map { it[KEY_REMINDER_ENABLED] ?: false }
    val reminderHourFlow: Flow<Int> = context.dataStore.data.map { it[KEY_REMINDER_HOUR] ?: 20 }
    val reminderMinuteFlow: Flow<Int> = context.dataStore.data.map { it[KEY_REMINDER_MINUTE] ?: 0 }
    val lastPracticedIndexFlow: Flow<Int> = context.dataStore.data.map { it[KEY_LAST_INDEX] ?: 0 }
    val userNameFlow: Flow<String> = context.dataStore.data.map { it[KEY_USER_NAME] ?: "" }

    suspend fun setLanguage(language: AppLanguage) {
        context.dataStore.edit { it[KEY_LANGUAGE] = language.name }
    }

    suspend fun setTheme(theme: AppTheme) {
        context.dataStore.edit { it[KEY_THEME] = theme.name }
    }

    suspend fun setReminderEnabled(enabled: Boolean) {
        context.dataStore.edit { it[KEY_REMINDER_ENABLED] = enabled }
    }

    suspend fun setReminderTime(hour: Int, minute: Int) {
        context.dataStore.edit {
            it[KEY_REMINDER_HOUR] = hour
            it[KEY_REMINDER_MINUTE] = minute
        }
    }

    suspend fun setLastPracticedIndex(index: Int) {
        context.dataStore.edit { it[KEY_LAST_INDEX] = index }
    }

    suspend fun setUserName(name: String) {
        context.dataStore.edit { it[KEY_USER_NAME] = name }
    }
}
