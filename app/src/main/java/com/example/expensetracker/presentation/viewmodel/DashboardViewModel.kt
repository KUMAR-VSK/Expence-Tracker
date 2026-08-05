package com.example.expensetracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.domain.model.BudgetPeriod
import com.example.expensetracker.domain.model.Expense
import com.example.expensetracker.domain.model.TransactionType
import com.example.expensetracker.domain.usecase.GetBudgetsUseCase
import com.example.expensetracker.domain.usecase.GetExpensesUseCase
import com.example.expensetracker.domain.usecase.ToggleFavoriteUseCase
import com.example.expensetracker.domain.usecase.DeleteExpenseUseCase
import com.example.expensetracker.utils.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class DashboardState(
    val totalBalance: Double = 0.0,
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val monthlyBudget: Double = 0.0,
    val budgetRemaining: Double = 0.0,
    val budgetProgress: Float = 0.0f,
    val todaySpending: Double = 0.0,
    val weeklySpending: Double = 0.0,
    val monthlySpending: Double = 0.0,
    val avgDailySpending: Double = 0.0,
    val highestExpense: Double = 0.0,
    val transactionCount: Int = 0,
    val recentTransactions: List<Expense> = emptyList()
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getExpensesUseCase: GetExpensesUseCase,
    private val getBudgetsUseCase: GetBudgetsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val deleteExpenseUseCase: DeleteExpenseUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardState())
    val uiState: StateFlow<DashboardState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                getExpensesUseCase(),
                getBudgetsUseCase()
            ) { expenses, budgets ->
                val now = System.currentTimeMillis()
                
                val startOfToday = DateUtils.getStartOfDay(now)
                val endOfToday = DateUtils.getEndOfDay(now)
                
                val startOfWeek = DateUtils.getStartOfWeek(now)
                val endOfWeek = DateUtils.getEndOfWeek(now)
                
                val startOfMonth = DateUtils.getStartOfMonth(now)
                val endOfMonth = DateUtils.getEndOfMonth(now)

                // Summaries
                var totalInc = 0.0
                var totalExp = 0.0
                var todaySpend = 0.0
                var weeklySpend = 0.0
                var monthlySpend = 0.0
                var maxExp = 0.0
                var expCount = 0

                val uniqueDays = mutableSetOf<Long>()

                expenses.forEach { e ->
                    if (e.type == TransactionType.INCOME) {
                        totalInc += e.amount
                    } else {
                        totalExp += e.amount
                        expCount++
                        if (e.amount > maxExp) {
                            maxExp = e.amount
                        }
                        
                        // Today, Week, Month spending
                        if (e.dateLong in startOfToday..endOfToday) {
                            todaySpend += e.amount
                        }
                        if (e.dateLong in startOfWeek..endOfWeek) {
                            weeklySpend += e.amount
                        }
                        if (e.dateLong in startOfMonth..endOfMonth) {
                            monthlySpend += e.amount
                        }
                        
                        uniqueDays.add(DateUtils.getStartOfDay(e.dateLong))
                    }
                }

                val balance = totalInc - totalExp

                // Get monthly global budget
                val activeBudget = budgets.find {
                    it.period == BudgetPeriod.MONTHLY && it.categoryId == null &&
                    now in it.startDate..it.endDate
                }
                
                val budgetVal = activeBudget?.amount ?: 0.0
                val budgetRemain = if (budgetVal > 0.0) budgetVal - monthlySpend else 0.0
                val progress = if (budgetVal > 0.0) (monthlySpend / budgetVal).toFloat() else 0.0f

                // Daily Average computation
                val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                val avgDaily = if (currentDay > 0) monthlySpend / currentDay else 0.0

                DashboardState(
                    totalBalance = balance,
                    totalIncome = totalInc,
                    totalExpense = totalExp,
                    monthlyBudget = budgetVal,
                    budgetRemaining = budgetRemain,
                    budgetProgress = progress,
                    todaySpending = todaySpend,
                    weeklySpending = weeklySpend,
                    monthlySpending = monthlySpend,
                    avgDailySpending = avgDaily,
                    highestExpense = maxExp,
                    transactionCount = expenses.size,
                    recentTransactions = expenses.take(10)
                )
            }.collect {
                _uiState.value = it
            }
        }
    }

    fun toggleFavorite(expense: Expense) {
        viewModelScope.launch {
            toggleFavoriteUseCase(expense.id, !expense.isFavorite)
        }
    }

    fun deleteTransaction(expense: Expense) {
        viewModelScope.launch {
            deleteExpenseUseCase(expense)
        }
    }
}
