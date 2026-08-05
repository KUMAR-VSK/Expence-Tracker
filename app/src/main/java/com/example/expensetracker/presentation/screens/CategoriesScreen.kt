package com.example.expensetracker.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.domain.model.Category
import com.example.expensetracker.presentation.components.ColorSelectDialog
import com.example.expensetracker.presentation.components.IconHelper
import com.example.expensetracker.presentation.components.IconSelectDialog
import com.example.expensetracker.presentation.viewmodel.CategoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    viewModel: CategoryViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categories by viewModel.categories.collectAsState()
    val formState by viewModel.formState.collectAsState()

    var isFormVisible by remember { mutableStateOf(false) }
    var showIconDialog by remember { mutableStateOf(false) }
    var showColorDialog by remember { mutableStateOf(false) }

    LaunchedEffect(formState.saveSuccess) {
        if (formState.saveSuccess) {
            isFormVisible = false
            viewModel.resetForm()
        }
    }

    val categoryColor = try {
        Color(android.graphics.Color.parseColor(formState.colorHex))
    } catch (e: Exception) {
        MaterialTheme.colorScheme.primary
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Categories", fontWeight = FontWeight.Bold) },
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
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Expandable form block
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
                            Text("Create Custom Category", fontWeight = FontWeight.Bold, fontSize = 14.sp)

                            // Name Input
                            OutlinedTextField(
                                value = formState.name,
                                onValueChange = { viewModel.onNameChange(it) },
                                label = { Text("Category Name") },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth(),
                                isError = formState.nameError != null
                            )
                            if (formState.nameError != null) {
                                Text(
                                    text = formState.nameError!!,
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(start = 12.dp, top = 2.dp)
                                )
                            }

                            // Icon and Color Row Picker
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Icon
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                        .clickable { showIconDialog = true }
                                        .padding(horizontal = 12.dp)
                                ) {
                                    Icon(
                                        imageVector = IconHelper.getIconByName(formState.iconName),
                                        contentDescription = "",
                                        tint = categoryColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = "Icon: ${formState.iconName}", fontSize = 12.sp)
                                }

                                // Color
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                        .clickable { showColorDialog = true }
                                        .padding(horizontal = 12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(categoryColor)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = "Pick Color", fontSize = 12.sp)
                                }
                            }

                            Button(
                                onClick = { viewModel.saveCategory() },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Add Category")
                            }
                        }
                    }
                }
            }

            // Categories List
            items(categories, key = { it.id }) { cat ->
                val catColor = try {
                    Color(android.graphics.Color.parseColor(cat.colorHex))
                } catch (e: Exception) {
                    Color.Gray
                }

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
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
                                    imageVector = IconHelper.getIconByName(cat.iconName),
                                    contentDescription = cat.name,
                                    tint = catColor,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = cat.name, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                if (cat.isCustom) {
                                    Text("Custom", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Pin Icon
                            IconButton(onClick = { viewModel.togglePinned(cat) }) {
                                Icon(
                                    imageVector = if (cat.isPinned) Icons.Default.PushPin else Icons.Outlined.PushPin,
                                    contentDescription = "Pin Favorite",
                                    tint = if (cat.isPinned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            // Delete Action for Custom Categories Only
                            if (cat.isCustom) {
                                IconButton(onClick = { viewModel.deleteCategory(cat) }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Color & Icon picker overlays
        if (showIconDialog) {
            IconSelectDialog(
                onIconSelected = {
                    viewModel.onIconChange(it)
                    showIconDialog = false
                },
                onDismissRequest = { showIconDialog = false }
            )
        }

        if (showColorDialog) {
            ColorSelectDialog(
                onColorSelected = {
                    viewModel.onColorChange(it)
                    showColorDialog = false
                },
                onDismissRequest = { showColorDialog = false }
            )
        }
    }
}
