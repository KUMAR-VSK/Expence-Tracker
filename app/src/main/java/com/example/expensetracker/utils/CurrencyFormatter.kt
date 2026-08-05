package com.example.expensetracker.utils

import java.util.Locale

object CurrencyFormatter {
    fun formatAmount(amount: Double, symbol: String, precision: Int): String {
        return try {
            val formatString = "%,.${precision}f"
            val formattedNumber = String.format(Locale.getDefault(), formatString, amount)
            "$symbol$formattedNumber"
        } catch (e: Exception) {
            "$symbol${String.format(Locale.getDefault(), "%,.2f", amount)}"
        }
    }
}
