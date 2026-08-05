package com.example.expensetracker.domain.model

data class Expense(
    val id: Long = 0,
    val amount: Double,
    val type: TransactionType, // Enum or wrapper
    val category: Category?,
    val dateLong: Long, // Epoch ms
    val timeString: String, // HH:mm
    val paymentMethod: PaymentMethod?,
    val notes: String,
    val location: String? = null,
    val tags: List<String> = emptyList(),
    val isFavorite: Boolean = false,
    val repeatInterval: String? = null // Default null / "None"
)

enum class TransactionType {
    INCOME, EXPENSE
}
