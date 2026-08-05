package com.example.expensetracker.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.domain.model.Category
import com.example.expensetracker.domain.model.Expense
import com.example.expensetracker.domain.model.PaymentMethod
import com.example.expensetracker.domain.model.TransactionType
import com.example.expensetracker.presentation.components.TransactionItem
import com.example.expensetracker.presentation.viewmodel.SortOrder
import com.example.expensetracker.presentation.viewmodel.TransactionViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HistoryScreen(
    viewModel: TransactionViewModel,
    currencySymbol: String,
    precision: Int,
    dateFormat: String,
    onTransactionClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val expenses by viewModel.filteredExpenses.collectAsState()
    val filterState by viewModel.filterState.collectAsState()
    val categoriesList by viewModel.categories.collectAsState()
    val paymentList by viewModel.paymentMethods.collectAsState()

    val keyboardController = LocalSoftwareKeyboardController.current

    var isFilterVisible by remember { mutableStateOf(false) }
    var selectedIds by remember { mutableStateOf(emptySet<Long>()) }
    val isSelectionMode = selectedIds.isNotEmpty()

    Scaffold(
        topBar = {
            if (isSelectionMode) {
                TopAppBar(
                    title = { Text(text = "${selectedIds.size} Selected", fontWeight = FontWeight.Bold) },
                    actions = {
                        IconButton(
                            onClick = {
                                viewModel.deleteTransactions(selectedIds.toList())
                                selectedIds = emptySet()
                            }
                        ) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Selected", tint = MaterialTheme.colorScheme.error)
                        }
                        IconButton(onClick = { selectedIds = emptySet() }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Clear Selection")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                )
            } else {
                TopAppBar(
                    title = { Text(text = "Transaction History", fontWeight = FontWeight.Bold) },
                    actions = {
                        IconButton(onClick = { isFilterVisible = !isFilterVisible }) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "Filters",
                                tint = if (isFilterVisible) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
                )
            }
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search Input Row
            if (!isSelectionMode) {
                OutlinedTextField(
                    value = filterState.query,
                    onValueChange = { viewModel.onQueryChange(it) },
                    placeholder = { Text("Search by notes, tags, category...") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search") },
                    trailingIcon = {
                        if (filterState.query.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onQueryChange("") }) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Clear Search")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { keyboardController?.hide() }),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            // Expanding Filters Section
            AnimatedVisibility(
                visible = isFilterVisible && !isSelectionMode,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(text = "Filters", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        
                        // Transaction Types
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = "Transaction Type", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                val types = listOf(TransactionType.INCOME, TransactionType.EXPENSE)
                                types.forEach { type ->
                                    val isSelected = filterState.selectedTypes.contains(type)
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { viewModel.toggleTypeFilter(type) },
                                        label = { Text(type.name) }
                                    )
                                }
                            }
                        }

                        // Categories List
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = "Categories", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(categoriesList) { cat ->
                                    val isSelected = filterState.selectedCategoryIds.contains(cat.id)
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { viewModel.toggleCategoryFilter(cat.id) },
                                        label = { Text(cat.name) }
                                    )
                                }
                            }
                        }

                        // Payment Methods
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = "Payment Methods", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(paymentList) { pm ->
                                    val isSelected = filterState.selectedPaymentIds.contains(pm.id)
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { viewModel.togglePaymentFilter(pm.id) },
                                        label = { Text(pm.name) }
                                    )
                                }
                            }
                        }

                        // Sorting selection
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = "Sort By", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                val sortOptions = listOf(
                                    SortOrder.NEWEST to "Newest First",
                                    SortOrder.OLDEST to "Oldest First",
                                    SortOrder.HIGHEST to "Highest Amount",
                                    SortOrder.LOWEST to "Lowest Amount"
                                )
                                sortOptions.forEach { (option, label) ->
                                    FilterChip(
                                        selected = filterState.sortBy == option,
                                        onClick = { viewModel.onSortChange(option) },
                                        label = { Text(label) }
                                    )
                                }
                            }
                        }

                        // Reset button
                        TextButton(
                            onClick = { viewModel.resetFilters() },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Reset All Filters")
                        }
                    }
                }
            }

            // Transactions list
            if (expenses.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(text = "No matching transactions found.", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(expenses, key = { it.id }) { item ->
                        val isChecked = selectedIds.contains(item.id)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (isSelectionMode) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { checked ->
                                        selectedIds = if (checked) {
                                            selectedIds + item.id
                                        } else {
                                            selectedIds - item.id
                                        }
                                    }
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                            }
                            TransactionItem(
                                expense = item,
                                currencySymbol = currencySymbol,
                                precision = precision,
                                dateFormat = dateFormat,
                                onClick = {
                                    if (isSelectionMode) {
                                        selectedIds = if (isChecked) selectedIds - item.id else selectedIds + item.id
                                    } else {
                                        onTransactionClick(item.id)
                                    }
                                },
                                onDelete = { viewModel.deleteTransaction(item) },
                                onFavoriteToggle = { viewModel.toggleFavorite(item) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}
