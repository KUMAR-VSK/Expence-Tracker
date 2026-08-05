package com.example.expensetracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val period: String, // "DAILY", "WEEKLY", "MONTHLY"
    val categoryId: Long? = null, // Null means global budget
    val startDate: Long,
    val endDate: Long
)
