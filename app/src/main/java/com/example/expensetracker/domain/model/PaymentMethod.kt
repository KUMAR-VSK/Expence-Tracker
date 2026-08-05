package com.example.expensetracker.domain.model

data class PaymentMethod(
    val id: Long = 0,
    val name: String,
    val iconName: String,
    val isCustom: Boolean = false
)
