package com.example.expensetracker.domain.model

data class Budget(
    val id: Long = 0,
    val amount: Double,
    val period: BudgetPeriod,
    val categoryId: Long? = null, // Null means global budget
    val category: Category? = null,
    val startDate: Long,
    val endDate: Long
)

enum class BudgetPeriod {
    DAILY, WEEKLY, MONTHLY
}
