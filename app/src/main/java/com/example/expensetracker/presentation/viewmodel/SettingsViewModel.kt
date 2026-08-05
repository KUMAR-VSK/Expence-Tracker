package com.example.expensetracker.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.database.AppDatabase
import com.example.expensetracker.data.model.BudgetEntity
import com.example.expensetracker.data.model.CategoryEntity
import com.example.expensetracker.data.model.ExpenseEntity
import com.example.expensetracker.data.model.PaymentMethodEntity
import com.example.expensetracker.domain.model.AppSettings
import com.example.expensetracker.domain.usecase.GetSettingsUseCase
import com.example.expensetracker.domain.usecase.SaveSettingsUseCase
import com.example.expensetracker.utils.BackupHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getSettingsUseCase: GetSettingsUseCase,
    private val saveSettingsUseCase: SaveSettingsUseCase,
    private val appDatabase: AppDatabase
) : ViewModel() {

    val settings: StateFlow<AppSettings> = getSettingsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppSettings()
        )

    fun updateTheme(isDarkMode: Boolean?) {
        viewModelScope.launch {
            val current = getSettingsUseCase.getDirect()
            saveSettingsUseCase(current.copy(isDarkMode = isDarkMode))
        }
    }

    fun updateCurrency(code: String, symbol: String) {
        viewModelScope.launch {
            val current = getSettingsUseCase.getDirect()
            saveSettingsUseCase(current.copy(currencyCode = code, currencySymbol = symbol))
        }
    }

    fun updateDecimalPrecision(precision: Int) {
        viewModelScope.launch {
            val current = getSettingsUseCase.getDirect()
            saveSettingsUseCase(current.copy(decimalPrecision = precision))
        }
    }

    fun updateDateFormat(format: String) {
        viewModelScope.launch {
            val current = getSettingsUseCase.getDirect()
            saveSettingsUseCase(current.copy(dateFormat = format))
        }
    }

    fun updateTimeFormat(format: String) {
        viewModelScope.launch {
            val current = getSettingsUseCase.getDirect()
            saveSettingsUseCase(current.copy(timeFormat = format))
        }
    }

    // --- Reset Data ---
    fun resetAllData() {
        viewModelScope.launch(Dispatchers.IO) {
            appDatabase.clearAllTables()
            // We could re-trigger callback prepopulate or let database creation run.
            // Since db is already created, let's manually write defaults after clear.
            prepopulateDefaults()
        }
    }

    private suspend fun prepopulateDefaults() {
        val defaultCategories = listOf(
            CategoryEntity(name = "Food", iconName = "restaurant", colorHex = "#FF9800"),
            CategoryEntity(name = "Travel", iconName = "directions_car", colorHex = "#2196F3"),
            CategoryEntity(name = "Fuel", iconName = "local_gas_station", colorHex = "#00BCD4"),
            CategoryEntity(name = "Shopping", iconName = "shopping_bag", colorHex = "#E91E63"),
            CategoryEntity(name = "Bills", iconName = "receipt_long", colorHex = "#9C27B0"),
            CategoryEntity(name = "Rent", iconName = "home", colorHex = "#795548"),
            CategoryEntity(name = "Groceries", iconName = "shopping_cart", colorHex = "#4CAF50"),
            CategoryEntity(name = "Medical", iconName = "medical_services", colorHex = "#F44336"),
            CategoryEntity(name = "Insurance", iconName = "shield", colorHex = "#607D8B"),
            CategoryEntity(name = "Education", iconName = "school", colorHex = "#3F51B5"),
            CategoryEntity(name = "Entertainment", iconName = "sports_esports", colorHex = "#FFC107"),
            CategoryEntity(name = "Subscriptions", iconName = "card_membership", colorHex = "#673AB7"),
            CategoryEntity(name = "Salary", iconName = "payments", colorHex = "#009688"),
            CategoryEntity(name = "Freelancing", iconName = "work", colorHex = "#8BC34A"),
            CategoryEntity(name = "Investment", iconName = "trending_up", colorHex = "#00E676"),
            CategoryEntity(name = "Gift", iconName = "card_giftcard", colorHex = "#FF4081"),
            CategoryEntity(name = "Charity", iconName = "favorite", colorHex = "#FF5722"),
            CategoryEntity(name = "EMI", iconName = "account_balance", colorHex = "#E64A19"),
            CategoryEntity(name = "Others", iconName = "category", colorHex = "#9E9E9E")
        )
        appDatabase.categoryDao().insertCategories(defaultCategories)

        val defaultPaymentMethods = listOf(
            PaymentMethodEntity(name = "Cash", iconName = "money"),
            PaymentMethodEntity(name = "UPI", iconName = "qr_code"),
            PaymentMethodEntity(name = "Debit Card", iconName = "credit_card"),
            PaymentMethodEntity(name = "Credit Card", iconName = "credit_card"),
            PaymentMethodEntity(name = "Net Banking", iconName = "account_balance"),
            PaymentMethodEntity(name = "Wallet", iconName = "account_balance_wallet"),
            PaymentMethodEntity(name = "Others", iconName = "payment")
        )
        appDatabase.paymentMethodDao().insertPaymentMethods(defaultPaymentMethods)
    }

    // --- Backup & Restore ---

    suspend fun exportBackup(outputStream: OutputStream): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val categories = appDatabase.categoryDao().getAllCategories().first()
            val paymentMethods = appDatabase.paymentMethodDao().getAllPaymentMethods().first()
            val expenses = appDatabase.expenseDao().getAllExpenses().first()
            val budgets = appDatabase.budgetDao().getAllBudgets().first()

            val backupJson = BackupHelper.generateBackupString(
                categories = categories,
                paymentMethods = paymentMethods,
                expenses = expenses,
                budgets = budgets
            )
            outputStream.use { os ->
                os.write(backupJson.toByteArray())
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun importBackup(inputStream: InputStream): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val backupJson = inputStream.bufferedReader().use { it.readText() }
            val backupData = BackupHelper.restoreBackupString(backupJson)

            appDatabase.runInTransaction {
                // Clear existing
                appDatabase.clearAllTables()
                
                // Restore Categories
                val restoredCats = backupData.categories.map {
                    CategoryEntity(it.id, it.name, it.iconName, it.colorHex, it.isCustom, it.isPinned)
                }
                
                // Restore Payment Methods
                val restoredPMs = backupData.paymentMethods.map {
                    PaymentMethodEntity(it.id, it.name, it.iconName, it.isCustom)
                }
                
                // Restore Budgets
                val restoredBudgets = backupData.budgets.map {
                    BudgetEntity(it.id, it.amount, it.period, it.categoryId, it.startDate, it.endDate)
                }

                // Restore Expenses
                val restoredExpenses = backupData.expenses.map {
                    ExpenseEntity(it.id, it.amount, it.type, it.categoryId, it.dateLong, it.timeString, it.paymentMethodId, it.notes, it.location, it.tags, it.isFavorite, it.repeatInterval)
                }

                // Run insertions synchronously in transaction
                viewModelScope.launch(Dispatchers.IO) {
                    appDatabase.categoryDao().insertCategories(restoredCats)
                    appDatabase.paymentMethodDao().insertPaymentMethods(restoredPMs)
                    appDatabase.budgetDao().insertBudgets(restoredBudgets)
                    appDatabase.expenseDao().insertExpenses(restoredExpenses)
                    
                    // Add default settings back
                    saveSettingsUseCase(AppSettings())
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
