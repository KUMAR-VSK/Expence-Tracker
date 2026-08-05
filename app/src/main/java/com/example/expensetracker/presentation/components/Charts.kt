package com.example.expensetracker.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.ExpenseRed
import com.example.expensetracker.theme.IncomeGreen
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun DonutChart(
    data: Map<String, Double>,
    colors: Map<String, Color>,
    modifier: Modifier = Modifier
) {
    val total = data.values.sum()
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(data) {
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000)
        )
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(160.dp)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                var startAngle = -90f
                val strokeWidth = 24.dp.toPx()
                
                if (total == 0.0) {
                    drawArc(
                        color = Color.LightGray.copy(alpha = 0.3f),
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = strokeWidth)
                    )
                } else {
                    data.forEach { (label, value) ->
                        val sweepAngle = ((value / total) * 360f).toFloat() * animatedProgress.value
                        val sliceColor = colors[label] ?: Color.Gray

                        drawArc(
                            color = sliceColor,
                            startAngle = startAngle,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                        startAngle += sweepAngle
                    }
                }
            }
            
            // Central Text
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Total Spend",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                Text(
                    text = String.format("%.0f", total),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.width(20.dp))

        // Legends
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            val sortedData = data.entries.sortedByDescending { it.value }.take(5)
            sortedData.forEach { (label, value) ->
                val sliceColor = colors[label] ?: Color.Gray
                val pct = if (total > 0) (value / total * 100).toInt() else 0
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(sliceColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "$label ($pct%)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun LineChart(
    points: List<Double>,
    labels: List<String>,
    modifier: Modifier = Modifier
) {
    if (points.isEmpty()) return
    
    val animatedProgress = remember { Animatable(0f) }
    LaunchedEffect(points) {
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000)
        )
    }

    val maxVal = points.maxOrNull() ?: 1.0
    val minVal = points.minOrNull() ?: 0.0
    val diff = (maxVal - minVal).coerceAtLeast(1.0)

    val gridLineColor = MaterialTheme.colorScheme.outlineVariant
    val lineGradientStart = MaterialTheme.colorScheme.primary
    val lineGradientEnd = MaterialTheme.colorScheme.tertiary
    val textLabelStyle = TextStyle(
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
        fontSize = 9.sp
    )

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val spacingX = canvasWidth / (points.size - 1).coerceAtLeast(1)
                
                // Draw grid lines
                val numGridLines = 4
                for (i in 0..numGridLines) {
                    val y = (canvasHeight / numGridLines) * i
                    drawLine(
                        color = gridLineColor,
                        start = Offset(0f, y),
                        end = Offset(canvasWidth, y),
                        strokeWidth = 0.5.dp.toPx()
                    )
                }

                // Prepare line path
                val strokePath = Path()
                val fillPath = Path()

                points.forEachIndexed { i, valPoint ->
                    val pctY = ((valPoint - minVal) / diff).toFloat()
                    val x = spacingX * i
                    val y = canvasHeight - (pctY * canvasHeight * animatedProgress.value)

                    if (i == 0) {
                        strokePath.moveTo(x, y)
                        fillPath.moveTo(x, canvasHeight)
                        fillPath.lineTo(x, y)
                    } else {
                        strokePath.lineTo(x, y)
                        fillPath.lineTo(x, y)
                    }

                    if (i == points.lastIndex) {
                        fillPath.lineTo(x, canvasHeight)
                        fillPath.close()
                    }
                }

                // Draw gradient under the curve
                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            lineGradientStart.copy(alpha = 0.3f),
                            Color.Transparent
                        )
                    )
                )

                // Draw line path
                drawPath(
                    path = strokePath,
                    brush = Brush.linearGradient(
                        colors = listOf(lineGradientStart, lineGradientEnd)
                    ),
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )

                // Draw dots
                points.forEachIndexed { i, valPoint ->
                    val pctY = ((valPoint - minVal) / diff).toFloat()
                    val x = spacingX * i
                    val y = canvasHeight - (pctY * canvasHeight * animatedProgress.value)

                    drawCircle(
                        color = lineGradientStart,
                        radius = 4.dp.toPx(),
                        center = Offset(x, y)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 2.dp.toPx(),
                        center = Offset(x, y)
                    )
                }
            }
            
            // X-Axis Labels
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                labels.forEach { label ->
                    Text(text = label, style = textLabelStyle)
                }
            }
        }
    }
}

@Composable
fun ComparisonBarChart(
    income: List<Double>,
    expense: List<Double>,
    labels: List<String>,
    modifier: Modifier = Modifier
) {
    if (income.size != expense.size || income.isEmpty()) return

    val animatedProgress = remember { Animatable(0f) }
    LaunchedEffect(income, expense) {
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000)
        )
    }

    val maxVal = maxOf(income.maxOrNull() ?: 1.0, expense.maxOrNull() ?: 1.0).coerceAtLeast(1.0)
    val textLabelStyle = TextStyle(
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
        fontSize = 9.sp
    )

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val numItems = income.size
                
                val groupWidth = canvasWidth / numItems
                val barWidth = 10.dp.toPx()
                val spacingX = 4.dp.toPx()

                income.forEachIndexed { i, incVal ->
                    val expVal = expense[i]
                    
                    val xGroupCenter = groupWidth * i + groupWidth / 2f
                    
                    // Income Bar
                    val incPct = (incVal / maxVal).toFloat()
                    val incBarHeight = canvasHeight * incPct * animatedProgress.value
                    drawRoundRect(
                        color = IncomeGreen,
                        topLeft = Offset(xGroupCenter - barWidth - spacingX / 2f, canvasHeight - incBarHeight),
                        size = Size(barWidth, incBarHeight),
                        cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                    )

                    // Expense Bar
                    val expPct = (expVal / maxVal).toFloat()
                    val expBarHeight = canvasHeight * expPct * animatedProgress.value
                    drawRoundRect(
                        color = ExpenseRed,
                        topLeft = Offset(xGroupCenter + spacingX / 2f, canvasHeight - expBarHeight),
                        size = Size(barWidth, expBarHeight),
                        cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                labels.forEachIndexed { idx, label ->
                    Box(
                        modifier = Modifier.width(55.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = label, style = textLabelStyle)
                    }
                }
            }
        }
    }
}
