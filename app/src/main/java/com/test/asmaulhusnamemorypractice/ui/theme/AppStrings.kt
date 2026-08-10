package com.test.asmaulhusnamemorypractice.ui.theme

import androidx.compose.runtime.compositionLocalOf

data class AppStrings(
    // App Bar & Main
    val appName: String,
    val memoryPractice: String,
    val resetToFirst: String,
    
    // Practice Screen
    val practiceProgress: String,
    val progressCardTitle: String,
    val nameLabel: String,
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
    val version: String,
    val aboutApp: String,
    val auto: String,
    val dailyReminder: String,
    val everyDayAt: String,
    val changeTime: String,
    val open: String,
    
    // Bottom Nav Tabs
    val tabPractice: String,
    val tabExplore: String,
    val tabQuiz: String,
    
    // Misc
    val back: String,
    val noBrowserFound: String,
    
    // Quiz Screen
    val quizQuestion: String,
    val quizOf: String,
    val quizScore: String,
    val quizTryAgain: String,
    val quizShareResult: String,
    val quizPerfect: String,
    val quizGood: String,
    val quizTryHarder: String,
    val quizNextNameQuestion: String,
    val quizMeaningQuestion: String,
    val quizSequenceQuestion: String,
    
    // Sharing
    val shareProgressText: String,
    val shareQuizText: String,
    val shareChooserTitle: String,
    val shareImageProgressText: String,
    
    // Wisdom/Quote
    val dailyWisdom: String,
    val copy: String,
    val share: String,
    val copiedToClipboard: String
)

val EnglishStrings = AppStrings(
    appName = "Asmaul Husna",
    memoryPractice = "Memory Practice",
    resetToFirst = "Reset to Number 1",
    
    practiceProgress = "Practice Progress",
    progressCardTitle = "Practice Progress",
    nameLabel = "Name",
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
    indonesian = "Indonesia",
    theme = "Theme",
    light = "Light",
    dark = "Dark",
    legalAndInformation = "Legal & Information",
    privacyPolicy = "Privacy Policy",
    aboutApp = "About App",
    version = "Version",
    auto = "Auto",
    dailyReminder = "Daily Reminder",
    everyDayAt = "Every day at %s",
    changeTime = "Change Time",
    open = "Open",
    
    tabPractice = "Practice",
    tabExplore = "Explore",
    tabQuiz = "Quiz",
    
    back = "Back",
    noBrowserFound = "No browser found",
    
    quizQuestion = "Question",
    quizOf = "of",
    quizScore = "Final Score",
    quizTryAgain = "Try Again",
    quizShareResult = "Share Result",
    quizPerfect = "Perfect! Mashallah! 🌟",
    quizGood = "Good job! Keep practicing! 👍",
    quizTryHarder = "Don't give up! Let's try again. 💪",
    quizNextNameQuestion = "What comes after %s?",
    quizMeaningQuestion = "What is the meaning of %s?",
    quizSequenceQuestion = "Tap the names in the correct sequence",
    
    shareProgressText = "I just finished practicing Asmaul Husna! 🤲",
    shareQuizText = "I just finished the Asmaul Husna Quiz with a score of %s/%s! 🤲",
    shareChooserTitle = "Share Progress",
    shareImageProgressText = "I've memorized %s out of %s Asmaul Husna! 🤲",
    
    dailyWisdom = "Daily Wisdom",
    copy = "Copy",
    share = "Share",
    copiedToClipboard = "Copied to clipboard"
)

val IndonesianStrings = AppStrings(
    appName = "Asmaul Husna",
    memoryPractice = "Latihan Hafalan",
    resetToFirst = "Ulang ke Nomor 1",
    
    practiceProgress = "Progress Hafalan",
    progressCardTitle = "Progress Hafalan",
    nameLabel = "Nama",
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
    indonesian = "Indonesia",
    theme = "Tema",
    light = "Terang",
    dark = "Gelap",
    legalAndInformation = "Legal & Informasi",
    privacyPolicy = "Kebijakan Privasi",
    aboutApp = "Tentang Aplikasi",
    version = "Versi",
    auto = "Otomatis",
    dailyReminder = "Pengingat Latihan Harian",
    everyDayAt = "Setiap hari pukul %s",
    changeTime = "Ubah Waktu",
    open = "Buka",
    
    tabPractice = "Latihan",
    tabExplore = "Jelajahi",
    tabQuiz = "Kuis",
    
    back = "Kembali",
    noBrowserFound = "Browser tidak ditemukan",
    
    quizQuestion = "Pertanyaan",
    quizOf = "dari",
    quizScore = "Skor Akhir",
    quizTryAgain = "Coba Lagi",
    quizShareResult = "Bagikan Hasil",
    quizPerfect = "Sempurna! Masya Allah! 🌟",
    quizGood = "Bagus! Terus berlatih! 👍",
    quizTryHarder = "Jangan menyerah! Ayo coba lagi. 💪",
    quizNextNameQuestion = "Apa nama setelah %s?",
    quizMeaningQuestion = "Apa arti dari %s?",
    quizSequenceQuestion = "Ketuk nama-nama berikut dalam urutan yang benar",
    
    shareProgressText = "Saya baru saja menyelesaikan latihan hafalan Asmaul Husna! 🤲",
    shareQuizText = "Saya baru saja menyelesaikan Kuis Asmaul Husna dengan skor %s/%s! 🤲",
    shareChooserTitle = "Bagikan Progres",
    shareImageProgressText = "Saya sudah menghafal %s dari %s Asmaul Husna! 🤲",
    
    dailyWisdom = "Hikmah Harian",
    copy = "Salin",
    share = "Bagikan",
    copiedToClipboard = "Disalin ke papan klip"
)

val LocalAppStrings = compositionLocalOf { EnglishStrings }
