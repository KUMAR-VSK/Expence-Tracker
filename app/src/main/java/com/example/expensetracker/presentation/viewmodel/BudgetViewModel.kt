package com.example.expensetracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.domain.model.Budget
import com.example.expensetracker.domain.model.BudgetPeriod
import com.example.expensetracker.domain.model.Category
import com.example.expensetracker.domain.model.TransactionType
import com.example.expensetracker.domain.usecase.*
import com.example.expensetracker.utils.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BudgetItemProgress(
    val budget: Budget,
    val spent: Double,
    val remaining: Double,
    val progress: Float
)

data class BudgetFormState(
    val amount: String = "",
    val period: BudgetPeriod = BudgetPeriod.MONTHLY,
    val selectedCategory: Category? = null, // Null means Global
    val amountError: String? = null,
    val saveSuccess: Boolean = false
)

@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val getBudgetsUseCase: GetBudgetsUseCase,
    private val getExpensesUseCase: GetExpensesUseCase,
    private val addBudgetUseCase: AddBudgetUseCase,
    private val updateBudgetUseCase: UpdateBudgetUseCase,
    private val deleteBudgetUseCase: DeleteBudgetUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase
) : ViewModel() {

    private val _formState = MutableStateFlow(BudgetFormState())
    val formState: StateFlow<BudgetFormState> = _formState.asStateFlow()

    val categories: StateFlow<List<Category>> = getCategoriesUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Combines budgets with expense spending in those budget periods
    val budgetProgressList: StateFlow<List<BudgetItemProgress>> = combine(
        getBudgetsUseCase(),
        getExpensesUseCase()
    ) { budgets, expenses ->
        val now = System.currentTimeMillis()
        budgets.map { b ->
            // Filter expenses that fall in this budget time range and match category (if category-specific)
            val spentVal = expenses.filter { e ->
                e.type == TransactionType.EXPENSE &&
                e.dateLong in b.startDate..b.endDate &&
                (b.categoryId == null || e.category?.id == b.categoryId)
            }.sumOf { it.amount }

            val remain = b.amount - spentVal
            val progress = if (b.amount > 0) (spentVal / b.amount).toFloat() else 0f

            BudgetItemProgress(
                budget = b,
                spent = spentVal,
                remaining = remain,
                progress = progress
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Form Actions ---

    fun onAmountChange(value: String) {
        _formState.update { it.copy(amount = value, amountError = null) }
    }

    fun onPeriodChange(period: BudgetPeriod) {
        _formState.update { it.copy(period = period) }
    }

    fun onCategorySelect(category: Category?) {
        _formState.update { it.copy(selectedCategory = category) }
    }

    fun resetForm() {
        _formState.value = BudgetFormState()
    }

    fun saveBudget() {
        val state = _formState.value
        val amountVal = state.amount.toDoubleOrNull()

        if (amountVal == null || amountVal <= 0) {
            _formState.update { it.copy(amountError = "Enter a valid positive budget amount") }
            return
        }

        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val startMs: Long
            val endMs: Long

            when (state.period) {
                BudgetPeriod.DAILY -> {
                    startMs = DateUtils.getStartOfDay(now)
                    endMs = DateUtils.getEndOfDay(now)
                }
                BudgetPeriod.WEEKLY -> {
                    startMs = DateUtils.getStartOfWeek(now)
                    endMs = DateUtils.getEndOfWeek(now)
                }
                BudgetPeriod.MONTHLY -> {
                    startMs = DateUtils.getStartOfMonth(now)
                    endMs = DateUtils.getEndOfMonth(now)
                }
            }

            val budget = Budget(
                amount = amountVal,
                period = state.period,
                categoryId = state.selectedCategory?.id,
                startDate = startMs,
                endDate = endMs
            )

            val result = addBudgetUseCase(budget)
            if (result.isSuccess) {
                _formState.update { it.copy(saveSuccess = true) }
            }
        }
    }

    fun deleteBudget(budget: Budget) {
        viewModelScope.launch {
            deleteBudgetUseCase(budget)
        }
    }
}
