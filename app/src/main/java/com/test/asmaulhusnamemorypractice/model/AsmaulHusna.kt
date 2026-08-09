package com.test.asmaulhusnamemorypractice.model

/**
 * Domain model representing one of the 99 Asmaul Husna (Names of Allah).
 */
data class AsmaulHusna(
    val number: Int,
    val arabic: String,
    val latin: String,
    val meaningId: String,
    val meaningEn: String
)
