package com.example.expensetracker.presentation.screens

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material.icons.outlined.TableChart
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
import androidx.core.content.FileProvider
import com.example.expensetracker.domain.model.Expense
import com.example.expensetracker.domain.model.TransactionType
import com.example.expensetracker.presentation.viewmodel.TransactionViewModel
import com.example.expensetracker.theme.ExpenseRed
import com.example.expensetracker.theme.IncomeGreen
import com.example.expensetracker.utils.CurrencyFormatter
import com.example.expensetracker.utils.DateUtils
import com.example.expensetracker.utils.Exporter
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

enum class ReportPeriod {
    TODAY, YESTERDAY, WEEKLY, MONTHLY, QUARTERLY, YEARLY, CUSTOM
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    viewModel: TransactionViewModel,
    currencySymbol: String,
    precision: Int,
    dateFormat: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val expenses by viewModel.filteredExpenses.collectAsState()
    val context = LocalContext.current

    var selectedPeriod by remember { mutableStateOf(ReportPeriod.MONTHLY) }
    var startDateMs by remember { mutableStateOf(DateUtils.getStartOfMonth(System.currentTimeMillis())) }
    var endDateMs by remember { mutableStateOf(DateUtils.getEndOfMonth(System.currentTimeMillis())) }

    // Re-calculate dates when period changes
    LaunchedEffect(selectedPeriod) {
        val now = System.currentTimeMillis()
        val cal = Calendar.getInstance()
        cal.timeInMillis = now
        when (selectedPeriod) {
            ReportPeriod.TODAY -> {
                startDateMs = DateUtils.getStartOfDay(now)
                endDateMs = DateUtils.getEndOfDay(now)
            }
            ReportPeriod.YESTERDAY -> {
                cal.add(Calendar.DAY_OF_YEAR, -1)
                startDateMs = DateUtils.getStartOfDay(cal.timeInMillis)
                endDateMs = DateUtils.getEndOfDay(cal.timeInMillis)
            }
            ReportPeriod.WEEKLY -> {
                startDateMs = DateUtils.getStartOfWeek(now)
                endDateMs = DateUtils.getEndOfWeek(now)
            }
            ReportPeriod.MONTHLY -> {
                startDateMs = DateUtils.getStartOfMonth(now)
                endDateMs = DateUtils.getEndOfMonth(now)
            }
            ReportPeriod.QUARTERLY -> {
                cal.add(Calendar.MONTH, -3)
                startDateMs = DateUtils.getStartOfMonth(cal.timeInMillis)
                endDateMs = DateUtils.getEndOfMonth(now)
            }
            ReportPeriod.YEARLY -> {
                startDateMs = DateUtils.getStartOfYear(now)
                endDateMs = DateUtils.getEndOfYear(now)
            }
            ReportPeriod.CUSTOM -> { /* Keep custom input */ }
        }
    }

    // Filter expenses in range
    val periodExpenses = remember(expenses, startDateMs, endDateMs) {
        expenses.filter { it.dateLong in startDateMs..endDateMs }
    }

    val totalIncome = periodExpenses.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
    val totalExpense = periodExpenses.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
    val netSavings = totalIncome - totalExpense

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Financial Reports", fontWeight = FontWeight.Bold) },
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
            // Period selector tabs
            item {
                Text("Select Report Period", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val periods = listOf(
                        ReportPeriod.TODAY to "Today",
                        ReportPeriod.YESTERDAY to "Yesterday",
                        ReportPeriod.WEEKLY to "Weekly",
                        ReportPeriod.MONTHLY to "Monthly",
                        ReportPeriod.QUARTERLY to "Quarterly",
                        ReportPeriod.YEARLY to "Yearly",
                        ReportPeriod.CUSTOM to "Custom Date"
                    )
                    items(periods) { (period, label) ->
                        FilterChip(
                            selected = selectedPeriod == period,
                            onClick = { selectedPeriod = period },
                            label = { Text(label) }
                        )
                    }
                }
            }

            // Custom Range inputs
            if (selectedPeriod == ReportPeriod.CUSTOM) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Start Date
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                .clickable {
                                    val calendar = Calendar.getInstance().apply { timeInMillis = startDateMs }
                                    DatePickerDialog(
                                        context,
                                        { _, y, m, d ->
                                            val newCal = Calendar.getInstance().apply {
                                                set(Calendar.YEAR, y)
                                                set(Calendar.MONTH, m)
                                                set(Calendar.DAY_OF_MONTH, d)
                                            }
                                            startDateMs = DateUtils.getStartOfDay(newCal.timeInMillis)
                                        },
                                        calendar.get(Calendar.YEAR),
                                        calendar.get(Calendar.MONTH),
                                        calendar.get(Calendar.DAY_OF_MONTH)
                                    ).show()
                                }
                                .padding(horizontal = 12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = "", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = DateUtils.formatEpoch(startDateMs, "dd MMM yyyy"), fontSize = 12.sp)
                        }

                        // End Date
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                .clickable {
                                    val calendar = Calendar.getInstance().apply { timeInMillis = endDateMs }
                                    DatePickerDialog(
                                        context,
                                        { _, y, m, d ->
                                            val newCal = Calendar.getInstance().apply {
                                                set(Calendar.YEAR, y)
                                                set(Calendar.MONTH, m)
                                                set(Calendar.DAY_OF_MONTH, d)
                                            }
                                            endDateMs = DateUtils.getEndOfDay(newCal.timeInMillis)
                                        },
                                        calendar.get(Calendar.YEAR),
                                        calendar.get(Calendar.MONTH),
                                        calendar.get(Calendar.DAY_OF_MONTH)
                                    ).show()
                                }
                                .padding(horizontal = 12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = "", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = DateUtils.formatEpoch(endDateMs, "dd MMM yyyy"), fontSize = 12.sp)
                        }
                    }
                }
            }

            // Summary Info
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "Report Summary",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Income", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = CurrencyFormatter.formatAmount(totalIncome, currencySymbol, precision),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = IncomeGreen
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Expenses", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = CurrencyFormatter.formatAmount(totalExpense, currencySymbol, precision),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = ExpenseRed
                            )
                        }

                        Divider()

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Net Savings", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = CurrencyFormatter.formatAmount(netSavings, currencySymbol, precision),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (netSavings >= 0) IncomeGreen else ExpenseRed
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Transactions", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                            Text(
                                text = "${periodExpenses.size} items",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Export Actions
            item {
                Text("Export and Share Options", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // CSV Export
                    ExportRow(
                        title = "Export as CSV",
                        description = "Comma Separated Values document",
                        icon = Icons.Outlined.Description,
                        iconColor = Color(0xFF1976D2),
                        onClick = {
                            val csvStr = Exporter.exportToCsv(periodExpenses, dateFormat)
                            shareFile(context, "expense_report.csv", csvStr.toByteArray(), "text/comma-separated-values")
                        }
                    )

                    // Excel Export
                    ExportRow(
                        title = "Export as Excel (XML)",
                        description = "Formatted Microsoft Excel spreadsheet",
                        icon = Icons.Outlined.TableChart,
                        iconColor = Color(0xFF2E7D32),
                        onClick = {
                            val excelStr = Exporter.exportToExcelXml(periodExpenses, dateFormat)
                            shareFile(context, "expense_report.xls", excelStr.toByteArray(), "application/vnd.ms-excel")
                        }
                    )

                    // PDF Export
                    ExportRow(
                        title = "Export as PDF Document",
                        description = "Printable document report with table grids",
                        icon = Icons.Outlined.PictureAsPdf,
                        iconColor = Color(0xFFC62828),
                        onClick = {
                            val cacheFile = File(context.cacheDir, "expense_report.pdf")
                            FileOutputStream(cacheFile).use { fos ->
                                Exporter.exportToPdf(context, periodExpenses, dateFormat, fos)
                            }
                            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", cacheFile)
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "application/pdf"
                                putExtra(Intent.EXTRA_STREAM, uri)
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share PDF Report"))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ExportRow(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = "", tint = iconColor, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(text = description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            }
            Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = MaterialTheme.colorScheme.primary)
        }
    }
}

private fun shareFile(context: Context, filename: String, data: ByteArray, mimeType: String) {
    try {
        val cacheFile = File(context.cacheDir, filename)
        FileOutputStream(cacheFile).use { it.write(data) }

        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", cacheFile)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Financial Report"))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
