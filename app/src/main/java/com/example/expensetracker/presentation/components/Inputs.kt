package com.example.expensetracker.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.domain.model.TransactionType
import com.example.expensetracker.theme.ExpenseRed
import com.example.expensetracker.theme.IncomeGreen

@Composable
fun TransactionTypeToggle(
    selectedType: TransactionType,
    onTypeSelected: (TransactionType) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(25.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val types = listOf(TransactionType.EXPENSE, TransactionType.INCOME)
        types.forEach { type ->
            val isSelected = selectedType == type
            val activeColor = if (type == TransactionType.INCOME) IncomeGreen else ExpenseRed
            val backgroundColor by animateColorAsState(
                targetValue = if (isSelected) activeColor else Color.Transparent,
                label = "bgColor"
            )
            val textColor by animateColorAsState(
                targetValue = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                label = "textColor"
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(21.dp))
                    .background(backgroundColor)
                    .clickable { onTypeSelected(type) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (type == TransactionType.INCOME) "INCOME" else "EXPENSE",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = textColor,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
fun DecimalAmountInput(
    value: String,
    onValueChange: (String) -> Unit,
    currencySymbol: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = { input ->
            // Restrict input to numbers and max one decimal point
            if (input.isEmpty() || input.all { it.isDigit() || it == '.' }) {
                if (input.count { it == '.' } <= 1) {
                    onValueChange(input)
                }
            }
        },
        modifier = modifier.fillMaxWidth(),
        label = { Text("Amount") },
        leadingIcon = {
            Text(
                text = currencySymbol,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal,
            imeAction = ImeAction.Next
        ),
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        )
    )
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TagsInput(
    tags: List<String>,
    onTagsChange: (List<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    var text by remember { mutableStateOf("") }

    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Add Tags (notes / categories)") },
            placeholder = { Text("Type tag and click add or comma") },
            trailingIcon = {
                if (text.isNotBlank()) {
                    TextButton(
                        onClick = {
                            val cleanTag = text.trim()
                            if (cleanTag.isNotBlank() && cleanTag !in tags) {
                                onTagsChange(tags + cleanTag)
                            }
                            text = ""
                        }
                    ) {
                        Text("Add")
                    }
                }
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    val cleanTag = text.trim()
                    if (cleanTag.isNotBlank() && cleanTag !in tags) {
                        onTagsChange(tags + cleanTag)
                    }
                    text = ""
                }
            ),
            singleLine = true,
            shape = RoundedCornerShape(16.dp)
        )

        if (tags.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                tags.forEach { tag ->
                    InputChip(
                        selected = false,
                        onClick = {},
                        label = { Text(tag) },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove tag",
                                modifier = Modifier
                                    .size(14.dp)
                                    .clickable {
                                        onTagsChange(tags - tag)
                                    }
                            )
                        },
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }
        }
    }
}
