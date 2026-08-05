package com.example.expensetracker.utils

import com.example.expensetracker.data.model.BudgetEntity
import com.example.expensetracker.data.model.CategoryEntity
import com.example.expensetracker.data.model.ExpenseEntity
import com.example.expensetracker.data.model.PaymentMethodEntity
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class BackupData(
    val categories: List<BackupCategory>,
    val paymentMethods: List<BackupPaymentMethod>,
    val expenses: List<BackupExpense>,
    val budgets: List<BackupBudget>
)

@Serializable
data class BackupCategory(
    val id: Long,
    val name: String,
    val iconName: String,
    val colorHex: String,
    val isCustom: Boolean,
    val isPinned: Boolean
)

@Serializable
data class BackupPaymentMethod(
    val id: Long,
    val name: String,
    val iconName: String,
    val isCustom: Boolean
)

@Serializable
data class BackupExpense(
    val id: Long,
    val amount: Double,
    val type: String,
    val categoryId: Long,
    val dateLong: Long,
    val timeString: String,
    val paymentMethodId: Long,
    val notes: String,
    val location: String?,
    val tags: String,
    val isFavorite: Boolean,
    val repeatInterval: String?
)

@Serializable
data class BackupBudget(
    val id: Long,
    val amount: Double,
    val period: String,
    val categoryId: Long?,
    val startDate: Long,
    val endDate: Long
)

object BackupHelper {

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    fun generateBackupString(
        categories: List<CategoryEntity>,
        paymentMethods: List<PaymentMethodEntity>,
        expenses: List<ExpenseEntity>,
        budgets: List<BudgetEntity>
    ): String {
        val backupData = BackupData(
            categories = categories.map { BackupCategory(it.id, it.name, it.iconName, it.colorHex, it.isCustom, it.isPinned) },
            paymentMethods = paymentMethods.map { BackupPaymentMethod(it.id, it.name, it.iconName, it.isCustom) },
            expenses = expenses.map { BackupExpense(it.id, it.amount, it.type, it.categoryId, it.dateLong, it.timeString, it.paymentMethodId, it.notes, it.location, it.tags, it.isFavorite, it.repeatInterval) },
            budgets = budgets.map { BackupBudget(it.id, it.amount, it.period, it.categoryId, it.startDate, it.endDate) }
        )
        return json.encodeToString(BackupData.serializer(), backupData)
    }

    fun restoreBackupString(backupJson: String): BackupData {
        return json.decodeFromString(BackupData.serializer(), backupJson)
    }
}
