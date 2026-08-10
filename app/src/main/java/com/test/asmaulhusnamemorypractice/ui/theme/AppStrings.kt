package com.test.asmaulhusnamemorypractice.ui.theme

import androidx.compose.runtime.compositionLocalOf

data class AppStrings(
    // App Bar & Main
    val appName: String,
    val memoryPractice: String,
    val resetToFirst: String,
    
    // Practice Screen
    val practiceProgress: String,
    val tapCardToContinue: String,
    val completedTitle: String,
    val previous: String,
    val reset: String,
    val next: String,
    
    // Completion Dialog
    val victoryTrophy: String,
    val congratulations: String,
    val completionMessage: String,
    val startOver: String,
    val close: String,
    
    // Settings Screen
    val settings: String,
    val preferences: String,
    val language: String,
    val systemDefault: String,
    val english: String,
    val indonesian: String,
    val theme: String,
    val light: String,
    val dark: String,
    val legalAndInformation: String,
    val privacyPolicy: String,
    val aboutApp: String,
    val version: String
)

val EnglishStrings = AppStrings(
    appName = "Asmaul Husna",
    memoryPractice = "Memory Practice",
    resetToFirst = "Reset to Number 1",
    
    practiceProgress = "Practice Progress",
    tapCardToContinue = "Tap card to continue →",
    completedTitle = "Completed ✓",
    previous = "Previous",
    reset = "Reset",
    next = "Next",
    
    victoryTrophy = "Victory Trophy",
    congratulations = "Congratulations!",
    completionMessage = "You have completed the 99 Asmaul Husna practice!",
    startOver = "Start Over",
    close = "Close",
    
    settings = "Settings",
    preferences = "Preferences",
    language = "Language",
    systemDefault = "System Default",
    english = "English",
    indonesian = "Bahasa Indonesia",
    theme = "Theme",
    light = "Light",
    dark = "Dark",
    legalAndInformation = "Legal & Information",
    privacyPolicy = "Privacy Policy",
    aboutApp = "About App",
    version = "Version"
)

val IndonesianStrings = AppStrings(
    appName = "Asmaul Husna",
    memoryPractice = "Latihan Hafalan",
    resetToFirst = "Ulang ke Nomor 1",
    
    practiceProgress = "Progress Hafalan",
    tapCardToContinue = "Ketuk kartu untuk lanjut →",
    completedTitle = "Selesai ✓",
    previous = "Sebelumnya",
    reset = "Reset",
    next = "Lanjut",
    
    victoryTrophy = "Piala Kemenangan",
    congratulations = "Alhamdulillah!",
    completionMessage = "Anda telah menyelesaikan latihan 99 Asmaul Husna!",
    startOver = "Ulangi Dari Awal",
    close = "Tutup",
    
    settings = "Pengaturan",
    preferences = "Preferensi",
    language = "Bahasa",
    systemDefault = "Standar Sistem",
    english = "English",
    indonesian = "Bahasa Indonesia",
    theme = "Tema",
    light = "Terang",
    dark = "Gelap",
    legalAndInformation = "Legal & Informasi",
    privacyPolicy = "Kebijakan Privasi",
    aboutApp = "Tentang Aplikasi",
    version = "Versi"
)

val LocalAppStrings = compositionLocalOf { EnglishStrings }
