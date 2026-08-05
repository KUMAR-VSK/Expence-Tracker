package com.example.expensetracker.data.repository

import com.example.expensetracker.data.dao.*
import com.example.expensetracker.data.model.*
import com.example.expensetracker.domain.model.*
import com.example.expensetracker.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpenseRepositoryImpl @Inject constructor(
    private val expenseDao: ExpenseDao,
    private val categoryDao: CategoryDao,
    private val paymentMethodDao: PaymentMethodDao,
    private val budgetDao: BudgetDao,
    private val settingsDao: SettingsDao
) : ExpenseRepository {

    // --- Mapper Extensions ---

    private fun CategoryEntity.toDomain(): Category = Category(
        id = id,
        name = name,
        iconName = iconName,
        colorHex = colorHex,
        isCustom = isCustom,
        isPinned = isPinned
    )

    private fun Category.toEntity(): CategoryEntity = CategoryEntity(
        id = id,
        name = name,
        iconName = iconName,
        colorHex = colorHex,
        isCustom = isCustom,
        isPinned = isPinned
    )

    private fun PaymentMethodEntity.toDomain(): PaymentMethod = PaymentMethod(
        id = id,
        name = name,
        iconName = iconName,
        isCustom = isCustom
    )

    private fun PaymentMethod.toEntity(): PaymentMethodEntity = PaymentMethodEntity(
        id = id,
        name = name,
        iconName = iconName,
        isCustom = isCustom
    )

    private fun ExpenseEntity.toDomain(category: Category?, paymentMethod: PaymentMethod?): Expense = Expense(
        id = id,
        amount = amount,
        type = try { TransactionType.valueOf(type) } catch (e: Exception) { TransactionType.EXPENSE },
        category = category,
        dateLong = dateLong,
        timeString = timeString,
        paymentMethod = paymentMethod,
        notes = notes,
        location = location,
        tags = if (tags.isBlank()) emptyList() else tags.split(",").map { it.trim() }.filter { it.isNotEmpty() },
        isFavorite = isFavorite,
        repeatInterval = repeatInterval
    )

    private fun Expense.toEntity(): ExpenseEntity = ExpenseEntity(
        id = id,
        amount = amount,
        type = type.name,
        categoryId = category?.id ?: 0L,
        dateLong = dateLong,
        timeString = timeString,
        paymentMethodId = paymentMethod?.id ?: 0L,
        notes = notes,
        location = location,
        tags = tags.joinToString(","),
        isFavorite = isFavorite,
        repeatInterval = repeatInterval
    )

    private fun BudgetEntity.toDomain(category: Category?): Budget = Budget(
        id = id,
        amount = amount,
        period = try { BudgetPeriod.valueOf(period) } catch (e: Exception) { BudgetPeriod.MONTHLY },
        categoryId = categoryId,
        category = category,
        startDate = startDate,
        endDate = endDate
    )

    private fun Budget.toEntity(): BudgetEntity = BudgetEntity(
        id = id,
        amount = amount,
        period = period.name,
        categoryId = categoryId,
        startDate = startDate,
        endDate = endDate
    )

    private fun SettingsEntity.toDomain(): AppSettings = AppSettings(
        isDarkMode = isDarkMode,
        currencyCode = currencyCode,
        currencySymbol = currencySymbol,
        decimalPrecision = decimalPrecision,
        dateFormat = dateFormat,
        timeFormat = timeFormat,
        isPinLocked = isPinLocked,
        pinHash = pinHash
    )

    private fun AppSettings.toEntity(): SettingsEntity = SettingsEntity(
        id = 1,
        isDarkMode = isDarkMode,
        currencyCode = currencyCode,
        currencySymbol = currencySymbol,
        decimalPrecision = decimalPrecision,
        dateFormat = dateFormat,
        timeFormat = timeFormat,
        isPinLocked = isPinLocked,
        pinHash = pinHash
    )

    // --- Expenses Repository Implementations ---

    override fun getAllExpenses(): Flow<List<Expense>> {
        return combine(
            expenseDao.getAllExpenses(),
            categoryDao.getAllCategories(),
            paymentMethodDao.getAllPaymentMethods()
        ) { expenses, categories, paymentMethods ->
            val catMap = categories.associateBy { it.id }
            val pmMap = paymentMethods.associateBy { it.id }
            expenses.map { entity ->
                entity.toDomain(
                    category = catMap[entity.categoryId]?.toDomain(),
                    paymentMethod = pmMap[entity.paymentMethodId]?.toDomain()
                )
            }
        }
    }

    override suspend fun getExpenseById(id: Long): Expense? {
        val entity = expenseDao.getExpenseById(id) ?: return null
        val category = entity.categoryId.let { categoryDao.getCategoryById(it) }?.toDomain()
        val paymentMethod = entity.paymentMethodId.let { paymentMethodDao.getPaymentMethodById(it) }?.toDomain()
        return entity.toDomain(category, paymentMethod)
    }

    override fun getExpensesInRange(startDate: Long, endDate: Long): Flow<List<Expense>> {
        return combine(
            expenseDao.getExpensesInRange(startDate, endDate),
            categoryDao.getAllCategories(),
            paymentMethodDao.getAllPaymentMethods()
        ) { expenses, categories, paymentMethods ->
            val catMap = categories.associateBy { it.id }
            val pmMap = paymentMethods.associateBy { it.id }
            expenses.map { entity ->
                entity.toDomain(
                    category = catMap[entity.categoryId]?.toDomain(),
                    paymentMethod = pmMap[entity.paymentMethodId]?.toDomain()
                )
            }
        }
    }

    override suspend fun insertExpense(expense: Expense): Long {
        return expenseDao.insertExpense(expense.toEntity())
    }

    override suspend fun updateExpense(expense: Expense) {
        expenseDao.updateExpense(expense.toEntity())
    }

    override suspend fun deleteExpense(expense: Expense) {
        expenseDao.deleteExpense(expense.toEntity())
    }

    override suspend fun deleteExpenses(ids: List<Long>) {
        expenseDao.deleteExpenses(ids)
    }

    override suspend fun toggleFavorite(id: Long, isFavorite: Boolean) {
        expenseDao.updateFavoriteStatus(id, isFavorite)
    }

    override fun getTotalIncome(): Flow<Double> {
        return expenseDao.getTotalIncome().map { it ?: 0.0 }
    }

    override fun getTotalExpense(): Flow<Double> {
        return expenseDao.getTotalExpense().map { it ?: 0.0 }
    }

    override fun getExpenseSumInRange(startDate: Long, endDate: Long): Flow<Double> {
        return expenseDao.getExpenseSumInRange(startDate, endDate).map { it ?: 0.0 }
    }

    override fun getIncomeSumInRange(startDate: Long, endDate: Long): Flow<Double> {
        return expenseDao.getIncomeSumInRange(startDate, endDate).map { it ?: 0.0 }
    }

    // --- Categories Repository Implementations ---

    override fun getAllCategories(): Flow<List<Category>> {
        return categoryDao.getAllCategories().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getCategoryById(id: Long): Category? {
        return categoryDao.getCategoryById(id)?.toDomain()
    }

    override suspend fun insertCategory(category: Category): Long {
        return categoryDao.insertCategory(category.toEntity())
    }

    override suspend fun updateCategory(category: Category) {
        categoryDao.updateCategory(category.toEntity())
    }

    override suspend fun deleteCategory(category: Category) {
        categoryDao.deleteCategory(category.toEntity())
    }

    // --- Payment Methods Repository Implementations ---

    override fun getAllPaymentMethods(): Flow<List<PaymentMethod>> {
        return paymentMethodDao.getAllPaymentMethods().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getPaymentMethodById(id: Long): PaymentMethod? {
        return paymentMethodDao.getPaymentMethodById(id)?.toDomain()
    }

    override suspend fun insertPaymentMethod(paymentMethod: PaymentMethod): Long {
        return paymentMethodDao.insertPaymentMethod(paymentMethod.toEntity())
    }

    override suspend fun updatePaymentMethod(paymentMethod: PaymentMethod) {
        paymentMethodDao.updatePaymentMethod(paymentMethod.toEntity())
    }

    override suspend fun deletePaymentMethod(paymentMethod: PaymentMethod) {
        paymentMethodDao.deletePaymentMethod(paymentMethod.toEntity())
    }

    // --- Budgets Repository Implementations ---

    override fun getAllBudgets(): Flow<List<Budget>> {
        return combine(
            budgetDao.getAllBudgets(),
            categoryDao.getAllCategories()
        ) { budgets, categories ->
            val catMap = categories.associateBy { it.id }
            budgets.map { entity ->
                entity.toDomain(category = catMap[entity.categoryId]?.toDomain())
            }
        }
    }

    override suspend fun getBudgetById(id: Long): Budget? {
        val entity = budgetDao.getBudgetById(id) ?: return null
        val category = entity.categoryId?.let { categoryDao.getCategoryById(it) }?.toDomain()
        return entity.toDomain(category)
    }

    override fun getBudgetByPeriodAndCategory(period: BudgetPeriod, categoryId: Long?): Flow<List<Budget>> {
        return combine(
            budgetDao.getBudgetByPeriodAndCategory(period.name, categoryId),
            categoryDao.getAllCategories()
        ) { budgets, categories ->
            val catMap = categories.associateBy { it.id }
            budgets.map { entity ->
                entity.toDomain(category = catMap[entity.categoryId]?.toDomain())
            }
        }
    }

    override suspend fun insertBudget(budget: Budget): Long {
        return budgetDao.insertBudget(budget.toEntity())
    }

    override suspend fun updateBudget(budget: Budget) {
        budgetDao.updateBudget(budget.toEntity())
    }

    override suspend fun deleteBudget(budget: Budget) {
        budgetDao.deleteBudget(budget.toEntity())
    }

    // --- Settings Repository Implementations ---

    override fun getSettings(): Flow<AppSettings> {
        return settingsDao.getSettings().map {
            it?.toDomain() ?: AppSettings()
        }
    }

    override suspend fun getSettingsDirect(): AppSettings {
        return settingsDao.getSettingsDirect()?.toDomain() ?: AppSettings()
    }

    override suspend fun saveSettings(settings: AppSettings) {
        settingsDao.insertOrUpdateSettings(settings.toEntity())
    }
}
