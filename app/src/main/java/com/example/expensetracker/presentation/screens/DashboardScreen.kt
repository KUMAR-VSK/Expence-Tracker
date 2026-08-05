package com.example.expensetracker.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Leaderboard
import androidx.compose.material.icons.outlined.Wallet
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
import com.example.expensetracker.presentation.components.StatCard
import com.example.expensetracker.presentation.components.TransactionItem
import com.example.expensetracker.presentation.components.WalletCard
import com.example.expensetracker.presentation.viewmodel.DashboardViewModel
import com.example.expensetracker.theme.IncomeGreen
import com.example.expensetracker.utils.CurrencyFormatter
import com.example.expensetracker.utils.DateUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    userName: String,
    currencySymbol: String,
    precision: Int,
    dateFormat: String,
    onAddTransactionClick: () -> Unit,
    onTransactionClick: (Long) -> Unit,
    onSeeAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    val todayStr = SimpleDateFormat("EEEE, dd MMMM", Locale.getDefault()).format(Date())

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTransactionClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Transaction")
            }
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
            // Greeting & Date Header
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Hello, $userName 👋",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = todayStr,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            }

            // Wallet Balance Card
            item {
                WalletCard(
                    balance = state.totalBalance,
                    income = state.totalIncome,
                    expense = state.totalExpense,
                    budgetRemaining = state.budgetRemaining,
                    budgetProgress = state.budgetProgress,
                    currencySymbol = currencySymbol,
                    precision = precision
                )
            }

            // Period Statistics (Grid of 2 Stat Cards)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatCard(
                            title = "DAILY AVERAGE",
                            value = CurrencyFormatter.formatAmount(state.avgDailySpending, currencySymbol, precision),
                            icon = Icons.Outlined.TrendingUp,
                            iconColor = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "HIGHEST EXPENSE",
                            value = CurrencyFormatter.formatAmount(state.highestExpense, currencySymbol, precision),
                            icon = Icons.Outlined.Leaderboard,
                            iconColor = Color(0xFFFF5252),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatCard(
                            title = "TODAY'S SPENDING",
                            value = CurrencyFormatter.formatAmount(state.todaySpending, currencySymbol, precision),
                            icon = Icons.Outlined.History,
                            iconColor = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "TRANSACTIONS COUNT",
                            value = "${state.transactionCount}",
                            icon = Icons.Outlined.Wallet,
                            iconColor = IncomeGreen,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Recent Transactions header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Transactions",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "See All",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { onSeeAllClick() }
                    )
                }
            }

            // Recent Transactions List
            if (state.recentTransactions.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No transactions yet. Click + to add!",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            } else {
                items(state.recentTransactions, key = { it.id }) { item ->
                    TransactionItem(
                        expense = item,
                        currencySymbol = currencySymbol,
                        precision = precision,
                        dateFormat = dateFormat,
                        onClick = { onTransactionClick(item.id) },
                        onDelete = { viewModel.deleteTransaction(item) },
                        onFavoriteToggle = { viewModel.toggleFavorite(item) }
                    )
                }
            }
        }
    }
}
