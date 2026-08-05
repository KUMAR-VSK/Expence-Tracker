package com.example.expensetracker.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.domain.model.BudgetPeriod
import com.example.expensetracker.domain.model.Category
import com.example.expensetracker.presentation.components.CategorySelectDialog
import com.example.expensetracker.presentation.components.DecimalAmountInput
import com.example.expensetracker.presentation.components.IconHelper
import com.example.expensetracker.presentation.viewmodel.BudgetViewModel
import com.example.expensetracker.theme.ExpenseRed
import com.example.expensetracker.theme.IncomeGreen
import com.example.expensetracker.theme.TealAccent
import com.example.expensetracker.utils.CurrencyFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(
    viewModel: BudgetViewModel,
    currencySymbol: String,
    precision: Int,
    onBackClick: () -> Unit,
    onNavigateToCategories: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progressList by viewModel.budgetProgressList.collectAsState()
    val formState by viewModel.formState.collectAsState()
    val categoriesList by viewModel.categories.collectAsState()

    var isFormVisible by remember { mutableStateOf(false) }
    var showCategoryDialog by remember { mutableStateOf(false) }

    LaunchedEffect(formState.saveSuccess) {
        if (formState.saveSuccess) {
            isFormVisible = false
            viewModel.resetForm()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Budget Planner", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { isFormVisible = !isFormVisible }) {
                        Icon(
                            imageVector = if (isFormVisible) Icons.Default.Close else Icons.Default.Add,
                            contentDescription = "Toggle add form"
                        )
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
            // Expandable Add Budget Form Card
            item {
                AnimatedVisibility(visible = isFormVisible) {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("Create Budget", fontWeight = FontWeight.Bold, fontSize = 14.sp)

                            // Amount Input
                            Column {
                                DecimalAmountInput(
                                    value = formState.amount,
                                    onValueChange = { viewModel.onAmountChange(it) },
                                    currencySymbol = currencySymbol
                                )
                                if (formState.amountError != null) {
                                    Text(
                                        text = formState.amountError!!,
                                        color = MaterialTheme.colorScheme.error,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(start = 12.dp, top = 4.dp)
                                    )
                                }
                            }

                            // Period selector segmented row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .clip(RoundedCornerShape(22.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                    .padding(2.dp)
                            ) {
                                val periods = listOf(BudgetPeriod.DAILY, BudgetPeriod.WEEKLY, BudgetPeriod.MONTHLY)
                                periods.forEach { p ->
                                    val isSelected = formState.period == p
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight()
                                            .clip(RoundedCornerShape(20.dp))
                                            .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                                            .clickable { viewModel.onPeriodChange(p) }
                                    ) {
                                        Text(
                                            text = p.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            // Category Selector row (optional)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                    .clickable { showCategoryDialog = true }
                                    .padding(horizontal = 12.dp)
                            ) {
                                Icon(
                                    imageVector = if (formState.selectedCategory != null) IconHelper.getIconByName(formState.selectedCategory!!.iconName) else Icons.Default.Category,
                                    contentDescription = "",
                                    tint = if (formState.selectedCategory != null) Color(android.graphics.Color.parseColor(formState.selectedCategory!!.colorHex)) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = formState.selectedCategory?.name ?: "All Categories (Global Budget)",
                                    fontSize = 13.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                if (formState.selectedCategory != null) {
                                    IconButton(
                                        onClick = { viewModel.onCategorySelect(null) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                                    }
                                }
                            }

                            // Save button
                            Button(
                                onClick = { viewModel.saveBudget() },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Save Budget Limit")
                            }
                        }
                    }
                }
            }

            // Budget Progress List
            if (progressList.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No budgets defined. Click + to add limits!",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            } else {
                item {
                    Text(
                        text = "Active Budgets",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(progressList, key = { it.budget.id }) { item ->
                    val b = item.budget
                    val isGlobal = b.categoryId == null
                    val catColor = if (isGlobal) MaterialTheme.colorScheme.primary else Color(android.graphics.Color.parseColor(b.category?.colorHex ?: "#9E9E9E"))

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
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
                                            imageVector = if (isGlobal) Icons.Default.Category else IconHelper.getIconByName(b.category?.iconName ?: "category"),
                                            contentDescription = "",
                                            tint = catColor,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(text = if (isGlobal) "Global Budget" else b.category?.name ?: "", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                        Text(text = b.period.name, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                                    }
                                }

                                IconButton(onClick = { viewModel.deleteBudget(b) }) {
                                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Spent vs Limit
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Column {
                                    Text(text = "SPENT", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                                    Text(
                                        text = CurrencyFormatter.formatAmount(item.spent, currencySymbol, precision),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(text = "LIMIT", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                                    Text(
                                        text = CurrencyFormatter.formatAmount(b.amount, currencySymbol, precision),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            // Progress Bar
                            LinearProgressIndicator(
                                progress = { item.progress.coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = if (item.progress > 0.9f) ExpenseRed else TealAccent,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )

                            // Status / Remaining
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (item.remaining < 0) "Exceeded by ${CurrencyFormatter.formatAmount(-item.remaining, currencySymbol, precision)}"
                                           else "Remaining: ${CurrencyFormatter.formatAmount(item.remaining, currencySymbol, precision)}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (item.remaining < 0) ExpenseRed else IncomeGreen
                                )
                                Text(
                                    text = "${(item.progress * 100).toInt()}%",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (item.progress > 0.9f) ExpenseRed else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        if (showCategoryDialog) {
            CategorySelectDialog(
                categories = categoriesList,
                selectedCategory = formState.selectedCategory,
                onCategorySelected = {
                    viewModel.onCategorySelect(it)
                    showCategoryDialog = false
                },
                onCreateCustomClick = {
                    showCategoryDialog = false
                    onNavigateToCategories()
                },
                onDismissRequest = { showCategoryDialog = false }
            )
        }
    }
}
