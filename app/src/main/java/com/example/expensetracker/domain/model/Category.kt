package com.example.expensetracker.domain.model

data class Category(
    val id: Long = 0,
    val name: String,
    val iconName: String,
    val colorHex: String,
    val isCustom: Boolean = false,
    val isPinned: Boolean = false
)
