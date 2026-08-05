package com.example.expensetracker.domain.repository

import com.example.expensetracker.domain.model.*
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    // Expenses
    fun getAllExpenses(): Flow<List<Expense>>
    suspend fun getExpenseById(id: Long): Expense?
    fun getExpensesInRange(startDate: Long, endDate: Long): Flow<List<Expense>>
    suspend fun insertExpense(expense: Expense): Long
    suspend fun updateExpense(expense: Expense)
    suspend fun deleteExpense(expense: Expense)
    suspend fun deleteExpenses(ids: List<Long>)
    suspend fun toggleFavorite(id: Long, isFavorite: Boolean)
    fun getTotalIncome(): Flow<Double>
    fun getTotalExpense(): Flow<Double>
    fun getExpenseSumInRange(startDate: Long, endDate: Long): Flow<Double>
    fun getIncomeSumInRange(startDate: Long, endDate: Long): Flow<Double>

    // Categories
    fun getAllCategories(): Flow<List<Category>>
    suspend fun getCategoryById(id: Long): Category?
    suspend fun insertCategory(category: Category): Long
    suspend fun updateCategory(category: Category)
    suspend fun deleteCategory(category: Category)

    // Payment Methods
    fun getAllPaymentMethods(): Flow<List<PaymentMethod>>
    suspend fun getPaymentMethodById(id: Long): PaymentMethod?
    suspend fun insertPaymentMethod(paymentMethod: PaymentMethod): Long
    suspend fun updatePaymentMethod(paymentMethod: PaymentMethod)
    suspend fun deletePaymentMethod(paymentMethod: PaymentMethod)

    // Budgets
    fun getAllBudgets(): Flow<List<Budget>>
    suspend fun getBudgetById(id: Long): Budget?
    fun getBudgetByPeriodAndCategory(period: BudgetPeriod, categoryId: Long?): Flow<List<Budget>>
    suspend fun insertBudget(budget: Budget): Long
    suspend fun updateBudget(budget: Budget)
    suspend fun deleteBudget(budget: Budget)

    // Settings
    fun getSettings(): Flow<AppSettings>
    suspend fun getSettingsDirect(): AppSettings
    suspend fun saveSettings(settings: AppSettings)
}
