package com.example.expensetracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.domain.model.Expense
import com.example.expensetracker.domain.model.TransactionType
import com.example.expensetracker.domain.usecase.GetExpensesUseCase
import com.example.expensetracker.utils.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import androidx.compose.ui.graphics.Color

enum class AnalyticsTimeframe {
    THIS_MONTH, LAST_MONTH, LAST_3_MONTHS, LAST_6_MONTHS, LAST_1_YEAR, LIFETIME
}

data class AnalyticsState(
    val selectedTimeframe: AnalyticsTimeframe = AnalyticsTimeframe.THIS_MONTH,
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val netSavings: Double = 0.0,
    val categoryDistribution: Map<String, Double> = emptyMap(),
    val categoryColors: Map<String, Color> = emptyMap(),
    val topSpendingCategories: List<Pair<String, Double>> = emptyList(),
    val dailyAverage: Double = 0.0,
    val highestSpendingDay: Pair<String, Double> = Pair("", 0.0),
    val lowestSpendingDay: Pair<String, Double> = Pair("", 0.0),
    val topExpenses: List<Expense> = emptyList(),
    // Chart coordinates
    val trendValues: List<Double> = emptyList(),
    val trendLabels: List<String> = emptyList(),
    // Income vs Expense comparison over months
    val monthlyIncomeTrend: List<Double> = emptyList(),
    val monthlyExpenseTrend: List<Double> = emptyList(),
    val monthlyTrendLabels: List<String> = emptyList()
)

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val getExpensesUseCase: GetExpensesUseCase
) : ViewModel() {

    private val _timeframe = MutableStateFlow(AnalyticsTimeframe.THIS_MONTH)
    val timeframe: StateFlow<AnalyticsTimeframe> = _timeframe.asStateFlow()

    private val _uiState = MutableStateFlow(AnalyticsState())
    val uiState: StateFlow<AnalyticsState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                getExpensesUseCase(),
                _timeframe
            ) { expenses, timeframe ->
                val now = System.currentTimeMillis()
                val filterStart = getStartTimeForTimeframe(timeframe, now)
                
                // Filter expenses for selected timeframe
                val periodExpenses = expenses.filter { 
                    (filterStart == null || it.dateLong >= filterStart) && it.dateLong <= now
                }

                var totalInc = 0.0
                var totalExp = 0.0
                val catDistribution = mutableMapOf<String, Double>()
                val catColorsMap = mutableMapOf<String, Color>()
                val daySpending = mutableMapOf<String, Double>()

                val expensesOnly = periodExpenses.filter { it.type == TransactionType.EXPENSE }

                periodExpenses.forEach { e ->
                    if (e.type == TransactionType.INCOME) {
                        totalInc += e.amount
                    } else {
                        totalExp += e.amount
                        
                        val catName = e.category?.name ?: "Others"
                        catDistribution[catName] = (catDistribution[catName] ?: 0.0) + e.amount
                        
                        val catColorHex = e.category?.colorHex ?: "#9E9E9E"
                        catColorsMap[catName] = try {
                            Color(android.graphics.Color.parseColor(catColorHex))
                        } catch (ex: Exception) {
                            Color.Gray
                        }

                        // Day aggregation
                        val dayStr = DateUtils.formatEpoch(e.dateLong, "dd MMM")
                        daySpending[dayStr] = (daySpending[dayStr] ?: 0.0) + e.amount
                    }
                }

                val savings = totalInc - totalExp

                // Top Spending Categories
                val topCats = catDistribution.entries
                    .sortedByDescending { it.value }
                    .map { Pair(it.key, it.value) }

                // Averages & Extreme days
                val daysCount = getDaysCountForTimeframe(timeframe, now)
                val dailyAvg = if (daysCount > 0) totalExp / daysCount else 0.0

                val maxDay = daySpending.entries.maxByOrNull { it.value }?.let { Pair(it.key, it.value) } ?: Pair("-", 0.0)
                val minDay = daySpending.entries.minByOrNull { it.value }?.let { Pair(it.key, it.value) } ?: Pair("-", 0.0)

                // Top 10 expenses
                val top10 = expensesOnly.sortedByDescending { it.amount }.take(10)

                // Trends calculation (e.g. split into 6 data points)
                val trendPoints = calculateTrendPoints(expensesOnly, timeframe, now)

                // Monthly Comparison Bars (Income vs Expense for last 6 months)
                val monthlyComparison = calculateMonthlyComparison(expenses, now)

                AnalyticsState(
                    selectedTimeframe = timeframe,
                    totalIncome = totalInc,
                    totalExpense = totalExp,
                    netSavings = savings,
                    categoryDistribution = catDistribution,
                    categoryColors = catColorsMap,
                    topSpendingCategories = topCats,
                    dailyAverage = dailyAvg,
                    highestSpendingDay = maxDay,
                    lowestSpendingDay = minDay,
                    topExpenses = top10,
                    trendValues = trendPoints.first,
                    trendLabels = trendPoints.second,
                    monthlyIncomeTrend = monthlyComparison.first,
                    monthlyExpenseTrend = monthlyComparison.second,
                    monthlyTrendLabels = monthlyComparison.third
                )
            }.collect {
                _uiState.value = it
            }
        }
    }

    fun setTimeframe(tf: AnalyticsTimeframe) {
        _timeframe.value = tf
    }

    private fun getStartTimeForTimeframe(tf: AnalyticsTimeframe, now: Long): Long? {
        val cal = Calendar.getInstance()
        cal.timeInMillis = now
        return when (tf) {
            AnalyticsTimeframe.THIS_MONTH -> DateUtils.getStartOfMonth(now)
            AnalyticsTimeframe.LAST_MONTH -> {
                cal.add(Calendar.MONTH, -1)
                DateUtils.getStartOfMonth(cal.timeInMillis)
            }
            AnalyticsTimeframe.LAST_3_MONTHS -> {
                cal.add(Calendar.MONTH, -2)
                DateUtils.getStartOfMonth(cal.timeInMillis)
            }
            AnalyticsTimeframe.LAST_6_MONTHS -> {
                cal.add(Calendar.MONTH, -5)
                DateUtils.getStartOfMonth(cal.timeInMillis)
            }
            AnalyticsTimeframe.LAST_1_YEAR -> {
                cal.add(Calendar.YEAR, -1)
                DateUtils.getStartOfYear(cal.timeInMillis)
            }
            AnalyticsTimeframe.LIFETIME -> null
        }
    }

    private fun getDaysCountForTimeframe(tf: AnalyticsTimeframe, now: Long): Int {
        val cal = Calendar.getInstance()
        val currentDay = cal.get(Calendar.DAY_OF_MONTH)
        return when (tf) {
            AnalyticsTimeframe.THIS_MONTH -> currentDay
            AnalyticsTimeframe.LAST_MONTH -> {
                cal.add(Calendar.MONTH, -1)
                cal.getActualMaximum(Calendar.DAY_OF_MONTH)
            }
            AnalyticsTimeframe.LAST_3_MONTHS -> 90
            AnalyticsTimeframe.LAST_6_MONTHS -> 180
            AnalyticsTimeframe.LAST_1_YEAR -> 365
            AnalyticsTimeframe.LIFETIME -> {
                // Return 30 or default unique active days if lifetime is large
                30
            }
        }
    }

    private fun calculateTrendPoints(
        expenses: List<Expense>,
        tf: AnalyticsTimeframe,
        now: Long
    ): Pair<List<Double>, List<String>> {
        // Return 6 chunks of data representing the time points
        val numPoints = 6
        val values = MutableList(numPoints) { 0.0 }
        val labels = MutableList(numPoints) { "" }

        val start = getStartTimeForTimeframe(tf, now) ?: (now - 30 * 24 * 60 * 60 * 1000L) // fallback 30 days
        val interval = (now - start) / numPoints

        for (i in 0 until numPoints) {
            val chunkStart = start + interval * i
            val chunkEnd = start + interval * (i + 1)
            val chunkExpenses = expenses.filter { it.dateLong in chunkStart..chunkEnd }
            values[i] = chunkExpenses.sumOf { it.amount }
            
            // Format center of chunk as label
            val midPoint = chunkStart + interval / 2
            labels[i] = DateUtils.formatEpoch(midPoint, if (tf == AnalyticsTimeframe.LAST_1_YEAR) "MMM" else "dd MMM")
        }

        return Pair(values, labels)
    }

    private fun calculateMonthlyComparison(
        expenses: List<Expense>,
        now: Long
    ): Triple<List<Double>, List<Double>, List<String>> {
        val size = 6
        val incs = MutableList(size) { 0.0 }
        val exps = MutableList(size) { 0.0 }
        val labels = MutableList(size) { "" }

        val cal = Calendar.getInstance()
        cal.timeInMillis = now

        for (i in (size - 1) downTo 0) {
            val idx = (size - 1) - i
            val startMs = DateUtils.getStartOfMonth(cal.timeInMillis)
            val endMs = DateUtils.getEndOfMonth(cal.timeInMillis)

            val monthExpenses = expenses.filter { it.dateLong in startMs..endMs }
            incs[idx] = monthExpenses.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
            exps[idx] = monthExpenses.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
            labels[idx] = SimpleDateFormat("MMM", Locale.getDefault()).format(Date(startMs))

            cal.add(Calendar.MONTH, -1) // Move back
        }

        return Triple(incs, exps, labels)
    }
}
