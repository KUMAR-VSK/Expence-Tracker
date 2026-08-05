package com.example.expensetracker.domain.model

data class AppSettings(
    val isDarkMode: Boolean? = null, // Follow system
    val currencyCode: String = "USD",
    val currencySymbol: String = "$",
    val decimalPrecision: Int = 2,
    val dateFormat: String = "dd MMM yyyy",
    val timeFormat: String = "hh:mm a",
    val isPinLocked: Boolean = false,
    val pinHash: String? = null,
    val userName: String = "Local User",
    val savingGoal: Double = 0.0
)
