package dev.sandesh.kharcha.feature.budget

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dev.sandesh.kharcha.core.designsystem.components.*
import dev.sandesh.kharcha.core.designsystem.theme.*
import dev.sandesh.kharcha.core.model.*

@Composable
fun BudgetRoute(
    navController: NavController,
    viewModel: BudgetListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    BudgetScreen(
        uiState = uiState,
        onAddBudget = { navController.navigate("budget/add") },
        onEditBudget = { id -> navController.navigate("budget/edit/$id") }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(
    uiState: BudgetListUiState,
    onAddBudget: () -> Unit,
    onEditBudget: (Long) -> Unit,
) {
    Scaffold(
        topBar = { KharchaTopBar(title = "Budget") },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddBudget) {
                Icon(Icons.Default.Add, "Add budget")
            }
        }
    ) { padding ->
        when {
            uiState.isLoading -> {
                Column(modifier = Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    repeat(4) { SkeletonLoader(modifier = Modifier.fillMaxWidth().height(100.dp)) }
                }
            }
            uiState.budgetProgresses.isEmpty() -> {
                EmptyState(
                    message = "No budgets set",
                    subtitle = "Set monthly limits for each spending category",
                    action = {
                        Button(onClick = onAddBudget) { Text("Add Budget") }
                    },
                    modifier = Modifier.padding(padding)
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.budgetProgresses, key = { it.budget.id }) { progress ->
                        BudgetProgressCard(
                            progress = progress,
                            onClick = { onEditBudget(progress.budget.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BudgetProgressCard(
    progress: BudgetProgress,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val alertColor = when (progress.alertLevel) {
        AlertLevel.RED -> ColorAlertRed
        AlertLevel.AMBER -> ColorAlertAmber
        AlertLevel.YELLOW -> ColorAlertYellow
        AlertLevel.GREEN -> ColorAlertGreen
    }

    KharchaCard(onClick = onClick, modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Circular progress ring
            BudgetRing(
                progress = progress.percentUsed,
                color = alertColor,
                modifier = Modifier.size(56.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(progress.category.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                Text(
                    "₹${String.format("%,.0f", progress.spent)} of ₹${String.format("%,.0f", progress.budget.amount)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(6.dp))
                BudgetProgressBar(percent = progress.percentUsed, height = 6.dp)
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "₹${String.format("%,.0f", progress.remaining)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = alertColor
                )
                Text(
                    "left",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun BudgetRing(
    progress: Float,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 800, easing = EaseOutCubic),
        label = "ring_progress"
    )

    val trackColor = MaterialTheme.colorScheme.surfaceVariant

    Box(
        modifier = modifier.drawBehind {
            val strokeWidth = size.width * 0.12f
            val radius = (size.width - strokeWidth) / 2
            val center = Offset(size.width / 2, size.height / 2)

            // Track ring
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            // Progress arc
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        },
        contentAlignment = Alignment.Center
    ) {
        Text(
            "${(progress * 100).toInt()}%",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}
