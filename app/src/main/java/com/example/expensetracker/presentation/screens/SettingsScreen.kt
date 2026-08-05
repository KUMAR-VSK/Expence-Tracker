package com.example.expensetracker.presentation.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.presentation.viewmodel.SettingsViewModel
import com.example.expensetracker.theme.ExpenseRed
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateToProfile: () -> Unit,
    onNavigateToCategories: () -> Unit,
    onNavigateToPayments: () -> Unit,
    onNavigateToAbout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.settings.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var showResetDialog by remember { mutableStateOf(false) }
    var showPinDialog by remember { mutableStateOf(false) }
    var pinInputValue by remember { mutableStateOf("") }

    // Backup Activity Launcher (SAF Create Document)
    val exportBackupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            coroutineScope.launch {
                val outputStream = context.contentResolver.openOutputStream(uri)
                if (outputStream != null) {
                    val result = viewModel.exportBackup(outputStream)
                    if (result.isSuccess) {
                        Toast.makeText(context, "Backup exported successfully!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Failed to export backup.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    // Restore Activity Launcher (SAF Open Document)
    val importBackupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            coroutineScope.launch {
                val inputStream = context.contentResolver.openInputStream(uri)
                if (inputStream != null) {
                    val result = viewModel.importBackup(inputStream)
                    if (result.isSuccess) {
                        Toast.makeText(context, "Data restored successfully! Please restart app.", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "Failed to restore backup.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings & Tools", fontWeight = FontWeight.Bold) },
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
            // General Settings Card
            item {
                Text("General", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(6.dp))
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column {
                        // User Profile row
                        SettingsRow(
                            title = "User Profile",
                            subtitle = "Edit username and goals",
                            icon = Icons.Default.Person,
                            onClick = onNavigateToProfile
                        )
                        Divider()

                        // Dark mode toggle row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = Icons.Default.BrightnessMedium, contentDescription = "", tint = MaterialTheme.colorScheme.primary)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Theme Mode", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Text("Toggle Light / Dark Mode", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                                }
                            }
                            Switch(
                                checked = settings.isDarkMode ?: false,
                                onCheckedChange = { viewModel.updateTheme(it) }
                            )
                        }
                    }
                }
            }

            // Localization Preferences
            item {
                Text("Formatting & Locale", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(6.dp))
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column {
                        // Decimal Precision selection row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = Icons.Default.Dialpad, contentDescription = "", tint = MaterialTheme.colorScheme.primary)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Decimal Places", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Text("Decimal Precision: ${settings.decimalPrecision}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                                }
                            }
                            
                            val precisions = listOf(0, 1, 2)
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                precisions.forEach { p ->
                                    val isSelected = settings.decimalPrecision == p
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                            .clickable { viewModel.updateDecimalPrecision(p) }
                                    ) {
                                        Text(text = "$p", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Security PIN Settings
            item {
                Text("Security", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(6.dp))
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = Icons.Default.Lock, contentDescription = "", tint = MaterialTheme.colorScheme.primary)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("PIN Passcode Lock", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Text(text = if (settings.isPinLocked) "App lock is enabled" else "No security lock", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                                }
                            }
                            Switch(
                                checked = settings.isPinLocked,
                                onCheckedChange = { checked ->
                                    if (checked) {
                                        showPinDialog = true
                                    } else {
                                        // Disable pin lock
                                        viewModel.setupPinLock(null, false)
                                    }
                                }
                            )
                        }
                    }
                }
            }

            // Database Tools
            item {
                Text("Backup & Database", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(6.dp))
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column {
                        SettingsRow(
                            title = "Export Local Backup",
                            subtitle = "Save a JSON file of your database offline",
                            icon = Icons.Default.CloudUpload,
                            onClick = {
                                exportBackupLauncher.launch("expense_tracker_backup.json")
                            }
                        )
                        Divider()

                        SettingsRow(
                            title = "Import Local Backup",
                            subtitle = "Restore database from a saved JSON file",
                            icon = Icons.Default.CloudDownload,
                            onClick = {
                                importBackupLauncher.launch(arrayOf("application/json"))
                            }
                        )
                        Divider()

                        SettingsRow(
                            title = "Reset All Data",
                            subtitle = "Permanently wipe everything and restore default categories",
                            icon = Icons.Default.DeleteForever,
                            iconColor = ExpenseRed,
                            onClick = { showResetDialog = true }
                        )
                    }
                }
            }

            // About section card link
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column {
                        SettingsRow(
                            title = "About Expense Tracker",
                            subtitle = "Version, privacy guidelines, and disclaimers",
                            icon = Icons.Default.Info,
                            onClick = onNavigateToAbout
                        )
                    }
                }
            }
        }

        // Pin Input Setup dialog
        if (showPinDialog) {
            AlertDialog(
                onDismissRequest = { showPinDialog = false },
                title = { Text("Setup Lock PIN") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Enter a 4-digit PIN code to secure your offline app lock:", fontSize = 13.sp)
                        OutlinedTextField(
                            value = pinInputValue,
                            onValueChange = { input ->
                                if (input.length <= 4 && input.all { it.isDigit() }) {
                                    pinInputValue = input
                                }
                            },
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (pinInputValue.length == 4) {
                                viewModel.setupPinLock(pinInputValue, true)
                                showPinDialog = false
                                pinInputValue = ""
                            } else {
                                Toast.makeText(context, "PIN must be exactly 4 digits", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Text("Enable Lock")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showPinDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Reset Data Warning Dialog
        if (showResetDialog) {
            AlertDialog(
                onDismissRequest = { showResetDialog = false },
                title = { Text("Wipe All Data?") },
                text = { Text("Are you absolutely sure you want to reset the database? This will delete all entered expenses and custom labels, reverting to fresh install categories. This action cannot be undone.") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.resetAllData()
                            showResetDialog = false
                            Toast.makeText(context, "All data wiped.", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ExpenseRed)
                    ) {
                        Text("Wipe Database", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResetDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun SettingsRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(iconColor.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = "", tint = iconColor, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(text = subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
        }
        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "", tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
    }
}
