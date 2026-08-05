package com.example.expensetracker.presentation.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.domain.model.Category
import com.example.expensetracker.domain.model.PaymentMethod
import com.example.expensetracker.presentation.components.*
import com.example.expensetracker.presentation.viewmodel.TransactionViewModel
import com.example.expensetracker.utils.DateUtils
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTransactionScreen(
    viewModel: TransactionViewModel,
    transactionId: Long?,
    currencySymbol: String,
    onBackClick: () -> Unit,
    onNavigateToCategories: () -> Unit,
    onNavigateToPayments: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.formState.collectAsState()
    val categoriesList by viewModel.categories.collectAsState()
    val paymentMethodsList by viewModel.paymentMethods.collectAsState()

    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var showCategoryDialog by remember { mutableStateOf(false) }
    var showPaymentDialog by remember { mutableStateOf(false) }

    LaunchedEffect(transactionId) {
        viewModel.resetForm()
        if (transactionId != null && transactionId > 0) {
            viewModel.loadFormFromExpense(transactionId)
        }
    }

    LaunchedEffect(state.saveSuccess) {
        if (state.saveSuccess) {
            onBackClick()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (transactionId != null) "Edit Transaction" else "Add Transaction",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // Income / Expense Toggle
            TransactionTypeToggle(
                selectedType = state.type,
                onTypeSelected = { viewModel.onTypeChange(it) }
            )

            // Amount Input
            Column {
                DecimalAmountInput(
                    value = state.amount,
                    onValueChange = { viewModel.onAmountChange(it) },
                    currencySymbol = currencySymbol
                )
                if (state.amountError != null) {
                    Text(
                        text = state.amountError!!,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(start = 12.dp, top = 4.dp)
                    )
                }
            }

            // Category Picker Row
            val categoryColor = try {
                Color(android.graphics.Color.parseColor(state.selectedCategory?.colorHex ?: "#9E9E9E"))
            } catch (e: Exception) {
                MaterialTheme.colorScheme.primary
            }

            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        .border(
                            width = 1.dp,
                            color = if (state.categoryError != null) MaterialTheme.colorScheme.error
                                    else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { showCategoryDialog = true }
                        .padding(horizontal = 16.dp)
                ) {
                    Icon(
                        imageVector = if (state.selectedCategory != null) IconHelper.getIconByName(state.selectedCategory!!.iconName) else Icons.Default.Category,
                        contentDescription = "Category",
                        tint = if (state.selectedCategory != null) categoryColor else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = state.selectedCategory?.name ?: "Select Category",
                        fontSize = 15.sp,
                        color = if (state.selectedCategory != null) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        fontWeight = if (state.selectedCategory != null) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (state.categoryError != null) {
                    Text(
                        text = state.categoryError!!,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(start = 12.dp, top = 4.dp)
                    )
                }
            }

            // Payment Method Picker Row
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        .border(
                            width = 1.dp,
                            color = if (state.paymentError != null) MaterialTheme.colorScheme.error
                                    else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { showPaymentDialog = true }
                        .padding(horizontal = 16.dp)
                ) {
                    Icon(
                        imageVector = if (state.selectedPaymentMethod != null) IconHelper.getIconByName(state.selectedPaymentMethod!!.iconName) else Icons.Default.Payment,
                        contentDescription = "Payment Method",
                        tint = if (state.selectedPaymentMethod != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = state.selectedPaymentMethod?.name ?: "Select Payment Method",
                        fontSize = 15.sp,
                        color = if (state.selectedPaymentMethod != null) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        fontWeight = if (state.selectedPaymentMethod != null) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (state.paymentError != null) {
                    Text(
                        text = state.paymentError!!,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(start = 12.dp, top = 4.dp)
                    )
                }
            }

            // Date & Time Picker Selector Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Date picker
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        .clickable {
                            val calendar = Calendar.getInstance().apply { timeInMillis = state.dateLong }
                            DatePickerDialog(
                                context,
                                { _, y, m, d ->
                                    val newCal = Calendar.getInstance().apply {
                                        set(Calendar.YEAR, y)
                                        set(Calendar.MONTH, m)
                                        set(Calendar.DAY_OF_MONTH, d)
                                    }
                                    viewModel.onDateChange(newCal.timeInMillis)
                                },
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        }
                        .padding(horizontal = 12.dp)
                ) {
                    Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = "Date", modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = DateUtils.formatEpoch(state.dateLong, "dd MMM yyyy"),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Time picker
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        .clickable {
                            val calendar = Calendar.getInstance()
                            TimePickerDialog(
                                context,
                                { _, h, m ->
                                    val ampm = if (h >= 12) "PM" else "AM"
                                    val cleanH = if (h % 12 == 0) 12 else h % 12
                                    val cleanM = String.format("%02d", m)
                                    viewModel.onTimeChange("$cleanH:$cleanM $ampm")
                                },
                                calendar.get(Calendar.HOUR_OF_DAY),
                                calendar.get(Calendar.MINUTE),
                                false
                            ).show()
                        }
                        .padding(horizontal = 12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Schedule, contentDescription = "Time", modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = state.timeString,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Notes field
            OutlinedTextField(
                value = state.notes,
                onValueChange = { viewModel.onNotesChange(it) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Notes (optional)") },
                leadingIcon = { Icon(imageVector = Icons.Default.Notes, contentDescription = "Notes") },
                singleLine = false,
                shape = RoundedCornerShape(16.dp)
            )

            // Location field
            OutlinedTextField(
                value = state.location ?: "",
                onValueChange = { viewModel.onLocationChange(it.ifBlank { null }) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Location (optional)") },
                leadingIcon = { Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Location") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )

            // Tags input
            TagsInput(
                tags = state.tags,
                onTagsChange = { viewModel.onTagsChange(it) }
            )

            // Favorite checkbox / toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f))
                    .clickable { viewModel.onFavoriteToggleForm(!state.isFavorite) }
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = "Favorite", tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = "Mark as Favorite", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
                Switch(
                    checked = state.isFavorite,
                    onCheckedChange = { viewModel.onFavoriteToggleForm(it) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Save & Cancel Buttons
            Button(
                onClick = { viewModel.saveTransaction(transactionId) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Save Transaction", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(80.dp))
        }

        // Overlay Picker Sheets
        if (showCategoryDialog) {
            CategorySelectDialog(
                categories = categoriesList,
                selectedCategory = state.selectedCategory,
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

        if (showPaymentDialog) {
            PaymentMethodSelectDialog(
                paymentMethods = paymentMethodsList,
                selectedMethod = state.selectedPaymentMethod,
                onMethodSelected = {
                    viewModel.onPaymentSelect(it)
                    showPaymentDialog = false
                },
                onCreateCustomClick = {
                    showPaymentDialog = false
                    onNavigateToPayments()
                },
                onDismissRequest = { showPaymentDialog = false }
            )
        }
    }
}
