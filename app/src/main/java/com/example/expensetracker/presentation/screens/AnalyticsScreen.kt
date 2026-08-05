package com.example.expensetracker.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.presentation.components.*
import com.example.expensetracker.presentation.viewmodel.AnalyticsTimeframe
import com.example.expensetracker.presentation.viewmodel.AnalyticsViewModel
import com.example.expensetracker.theme.ExpenseRed
import com.example.expensetracker.theme.IncomeGreen
import com.example.expensetracker.utils.CurrencyFormatter
import com.example.expensetracker.utils.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel,
    currencySymbol: String,
    precision: Int,
    dateFormat: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val selectedTimeframe by viewModel.timeframe.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Financial Analytics", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Timeframe Selector Tabs
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val timeframes = listOf(
                        AnalyticsTimeframe.THIS_MONTH to "This Month",
                        AnalyticsTimeframe.LAST_MONTH to "Last Month",
                        AnalyticsTimeframe.LAST_3_MONTHS to "3 Months",
                        AnalyticsTimeframe.LAST_6_MONTHS to "6 Months",
                        AnalyticsTimeframe.LAST_1_YEAR to "1 Year",
                        AnalyticsTimeframe.LIFETIME to "Lifetime"
                    )
                    items(timeframes) { (tf, label) ->
                        val isSelected = selectedTimeframe == tf
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setTimeframe(tf) },
                            label = { Text(label) }
                        )
                    }
                }
            }

            // Timeframe Summary Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Timeframe Summary", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("INCOME", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                                Text(
                                    text = CurrencyFormatter.formatAmount(state.totalIncome, currencySymbol, precision),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = IncomeGreen
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("EXPENSES", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                                Text(
                                    text = CurrencyFormatter.formatAmount(state.totalExpense, currencySymbol, precision),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ExpenseRed
                                )
                            }
                        }

                        Divider()

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Net Savings", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            Text(
                                text = CurrencyFormatter.formatAmount(state.netSavings, currencySymbol, precision),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (state.netSavings >= 0) IncomeGreen else ExpenseRed
                            )
                        }
                    }
                }
            }

            // Category Distribution Chart
            if (state.categoryDistribution.isNotEmpty()) {
                item {
                    Text("Spending by Category", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    DonutChart(
                        data = state.categoryDistribution,
                        colors = state.categoryColors
                    )
                }
            }

            // Spending Trend (LineChart)
            if (state.trendValues.isNotEmpty()) {
                item {
                    Text("Spending Trend", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    LineChart(
                        points = state.trendValues,
                        labels = state.trendLabels
                    )
                }
            }

            // Income vs Expense comparison (BarChart)
            if (state.monthlyIncomeTrend.isNotEmpty()) {
                item {
                    Text("Income vs Expense Trend", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    ComparisonBarChart(
                        income = state.monthlyIncomeTrend,
                        expense = state.monthlyExpenseTrend,
                        labels = state.monthlyTrendLabels
                    )
                }
            }

            // Top Expenses List Section
            if (state.topExpenses.isNotEmpty()) {
                item {
                    Text("Top 10 Spending Transactions", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                items(state.topExpenses) { expense ->
                    val colorHex = expense.category?.colorHex ?: "#9E9E9E"
                    val catColor = try { Color(android.graphics.Color.parseColor(colorHex)) } catch (e: Exception) { Color.Gray }
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(catColor.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = IconHelper.getIconByName(expense.category?.iconName ?: "category"),
                                    contentDescription = "",
                                    tint = catColor,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = expense.notes.ifBlank { expense.category?.name ?: "Others" }, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text(text = DateUtils.formatEpoch(expense.dateLong, dateFormat), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                            }
                        }
                        Text(
                            text = "-${CurrencyFormatter.formatAmount(expense.amount, currencySymbol, precision)}",
                            color = ExpenseRed,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}
