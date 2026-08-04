package dev.sandesh.kharcha.feature.transactions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import dev.sandesh.kharcha.core.model.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TransactionsRoute(
    navController: NavController,
    viewModel: TransactionListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    TransactionsScreen(
        uiState = uiState,
        onFilterChange = viewModel::setFilter,
        onSearchQueryChange = viewModel::setSearchQuery,
        onTransactionClick = { id -> navController.navigate("transactions/$id") },
        onAddClick = { navController.navigate("transactions/add") }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    uiState: TransactionListUiState,
    onFilterChange: (TransactionFilter) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onTransactionClick: (Long) -> Unit,
    onAddClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            Column {
                KharchaTopBar(
                    title = "Transactions",
                    actions = {
                        IconButton(onClick = onAddClick) {
                            Icon(Icons.Default.Add, contentDescription = "Add")
                        }
                    }
                )
                // Search bar
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = { Text("Search transactions...") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium
                )
                // Filter chips
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(TransactionFilter.values()) { filter ->
                        FilterChip(
                            selected = uiState.activeFilter == filter,
                            onClick = { onFilterChange(filter) },
                            label = { Text(filter.label) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        when {
            uiState.isLoading -> {
                Column(modifier = Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(8) { SkeletonLoader(modifier = Modifier.fillMaxWidth().height(72.dp)) }
                }
            }
            uiState.transactions.isEmpty() -> {
                EmptyState(
                    message = "No transactions found",
                    subtitle = if (uiState.searchQuery.isNotEmpty()) "Try a different search term" else "SMS transactions will appear here",
                    modifier = Modifier.padding(padding)
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Group by date
                    val grouped = uiState.transactions.groupBy { txn ->
                        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(txn.timestamp))
                    }
                    grouped.forEach { (date, txns) ->
                        item {
                            Text(
                                date,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                            )
                        }
                        items(txns, key = { it.id }) { txn ->
                            TransactionListItem(
                                transaction = txn,
                                onClick = { onTransactionClick(txn.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionListItem(
    transaction: Transaction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    KharchaCard(onClick = onClick, modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Status indicator dot
            if (transaction.status == TransactionStatus.NEEDS_REVIEW || transaction.status == TransactionStatus.LLM_PARSED) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.size(8.dp)
                ) {}
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    transaction.merchantNormalized.ifEmpty { transaction.merchantRaw.ifEmpty { "Unknown Merchant" } },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(transaction.timestamp)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (transaction.status == TransactionStatus.NEEDS_REVIEW) {
                        Text(
                            "• Review",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            AmountText(
                amount = transaction.amount,
                isCredit = transaction.type == TransactionType.CREDIT,
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}

enum class TransactionFilter(val label: String) {
    ALL("All"),
    DEBIT("Debits"),
    CREDIT("Credits"),
    NEEDS_REVIEW("Review"),
}
