package com.example.expensetracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey
    val id: Long = 1, // Single settings record with constant key
    val isDarkMode: Boolean? = null, // Null means follow system theme
    val currencyCode: String = "INR",
    val currencySymbol: String = "₹",
    val decimalPrecision: Int = 2,
    val dateFormat: String = "dd/MM/yyyy",
    val timeFormat: String = "HH:mm",
    val isPinLocked: Boolean = false,
    val pinHash: String? = null,
    val userName: String = "Local User",
    val savingGoal: Double = 0.0
)
