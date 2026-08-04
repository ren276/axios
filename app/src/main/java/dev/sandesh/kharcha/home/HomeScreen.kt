package dev.sandesh.kharcha.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dev.sandesh.kharcha.core.designsystem.components.*
import dev.sandesh.kharcha.core.designsystem.theme.ColorAlertAmber
import dev.sandesh.kharcha.core.designsystem.theme.ColorIncome
import dev.sandesh.kharcha.core.model.*

@Composable
fun HomeRoute(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    HomeScreen(
        uiState = uiState,
        onAddTransaction = { navController.navigate("transactions/add") },
        onTransactionClick = { id -> navController.navigate("transactions/$id") },
        onReviewAll = { navController.navigate("transactions/review") }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onAddTransaction: () -> Unit,
    onTransactionClick: (Long) -> Unit,
    onReviewAll: () -> Unit,
) {
    Scaffold(
        topBar = {
            KharchaTopBar(
                title = "Kharcha",
                actions = {
                    // Month selector could go here
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTransaction,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add transaction")
            }
        }
    ) { padding ->
        when {
            uiState.isLoading -> HomeSkeletonContent(modifier = Modifier.padding(padding))
            else -> HomeContent(
                uiState = uiState,
                onTransactionClick = onTransactionClick,
                onReviewAll = onReviewAll,
                modifier = Modifier.padding(padding)
            )
        }
    }
}

@Composable
private fun HomeContent(
    uiState: HomeUiState,
    onTransactionClick: (Long) -> Unit,
    onReviewAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ── Month Snapshot Card ──────────────────────────────────────────────
        item {
            MonthSnapshotCard(
                monthLabel = uiState.currentMonthLabel,
                totalExpense = uiState.monthExpense,
                totalIncome = uiState.monthIncome,
            )
        }

        // ── Needs Review Badge ───────────────────────────────────────────────
        if (uiState.needsReviewCount > 0) {
            item {
                Card(
                    onClick = onReviewAll,
                    colors = CardDefaults.cardColors(
                        containerColor = ColorAlertAmber.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = ColorAlertAmber)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "${uiState.needsReviewCount} transactions need review",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                "Tap to confirm or correct",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            Icons.Default.Add, // Chevron right
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // ── Budget Health ────────────────────────────────────────────────────
        if (uiState.budgetProgresses.isNotEmpty()) {
            item {
                Text(
                    "Budget Health",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            items(uiState.budgetProgresses.take(3)) { progress ->
                BudgetHealthRow(progress = progress)
            }
        }

        // ── Recent Transactions ──────────────────────────────────────────────
        item {
            Text(
                "Recent Transactions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        if (uiState.recentTransactions.isEmpty()) {
            item {
                EmptyState(
                    message = "No transactions yet",
                    subtitle = "SMS transactions will appear here automatically",
                    modifier = Modifier.padding(vertical = 32.dp)
                )
            }
        } else {
            items(uiState.recentTransactions, key = { it.id }) { txn ->
                TransactionRow(
                    transaction = txn,
                    onClick = { onTransactionClick(txn.id) }
                )
            }
        }
    }
}

@Composable
private fun MonthSnapshotCard(
    monthLabel: String,
    totalExpense: Double,
    totalIncome: Double,
    modifier: Modifier = Modifier
) {
    KharchaCard(modifier = modifier.fillMaxWidth()) {
        Text(
            monthLabel,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "₹${String.format("%,.2f", totalExpense)}",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            "spent this month",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            Column {
                Text("Income", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    "+₹${String.format("%,.0f", totalIncome)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = ColorIncome
                )
            }
            Column {
                Text("Expenses", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    "-₹${String.format("%,.0f", totalExpense)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun BudgetHealthRow(progress: BudgetProgress) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(progress.category.name, style = MaterialTheme.typography.bodyMedium)
                Text(
                    "₹${String.format("%,.0f", progress.spent)} / ₹${String.format("%,.0f", progress.budget.amount)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(4.dp))
            BudgetProgressBar(percent = progress.percentUsed)
        }
    }
}

@Composable
private fun TransactionRow(
    transaction: Transaction,
    onClick: () -> Unit
) {
    KharchaCard(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    transaction.merchantNormalized.ifEmpty { transaction.merchantRaw.ifEmpty { "Unknown" } },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    java.text.SimpleDateFormat("dd MMM, hh:mm a", java.util.Locale.getDefault())
                        .format(java.util.Date(transaction.timestamp)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            AmountText(
                amount = transaction.amount,
                isCredit = transaction.type == TransactionType.CREDIT,
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}

@Composable
private fun HomeSkeletonContent(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SkeletonLoader(modifier = Modifier.fillMaxWidth().height(120.dp))
        SkeletonLoader(modifier = Modifier.fillMaxWidth().height(60.dp))
        repeat(5) {
            SkeletonLoader(modifier = Modifier.fillMaxWidth().height(72.dp))
        }
    }
}
