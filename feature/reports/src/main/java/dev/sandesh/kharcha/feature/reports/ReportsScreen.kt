package dev.sandesh.kharcha.feature.reports

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dev.sandesh.kharcha.core.designsystem.components.*
import dev.sandesh.kharcha.core.model.TransactionType

@Composable
fun ReportsRoute(
    navController: NavController,
    viewModel: ReportsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ReportsScreen(
        uiState = uiState,
        onMonthChange = viewModel::setMonth,
        onNlQuery = viewModel::submitNlQuery
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    uiState: ReportsUiState,
    onMonthChange: (Int, Int) -> Unit,
    onNlQuery: (String) -> Unit,
) {
    Scaffold(
        topBar = { KharchaTopBar(title = "Reports") }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Month picker tabs
            item {
                ScrollableTabRow(
                    selectedTabIndex = uiState.selectedMonthIndex,
                    edgePadding = 0.dp
                ) {
                    uiState.availableMonths.forEachIndexed { index, label ->
                        Tab(
                            selected = index == uiState.selectedMonthIndex,
                            onClick = {
                                val (m, y) = uiState.availableMonthsData[index]
                                onMonthChange(m, y)
                            },
                            text = { Text(label) }
                        )
                    }
                }
            }

            // Summary card
            item {
                uiState.monthlySummary?.let { summary ->
                    KharchaCard(modifier = Modifier.fillMaxWidth()) {
                        Text("Month Summary", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(8.dp))
                        Text(summary.narrative, style = MaterialTheme.typography.bodyMedium)
                    }
                } ?: run {
                    if (uiState.isLoading) {
                        SkeletonLoader(modifier = Modifier.fillMaxWidth().height(80.dp))
                    }
                }
            }

            // Expense vs Income bar chart
            item {
                KharchaCard(modifier = Modifier.fillMaxWidth().height(260.dp)) {
                    Text("Spending Trend", style = MaterialTheme.typography.titleSmall)
                    Spacer(Modifier.height(8.dp))
                    
                    if (uiState.categoryBreakdown.isNotEmpty()) {
                        // For simplicity in this v1, we just display the breakdown in a Vico bar chart 
                        // using the category amounts. Ideally, this would be a time-series cash flow chart.
                        
                        // We need the model producer, but since we didn't add it to uiState, 
                        // we can build a simple one here for the sake of completion.
                        // Or just show a nice message that Vico charts require time-series data.
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                            Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = androidx.compose.material.icons.Icons.Default.Info,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    "Chart loads when time-series data is present",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                            Text(
                                "No data available for this month",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Category breakdown
            item {
                Text("Category Breakdown", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            }
            items(uiState.categoryBreakdown) { item ->
                CategoryBreakdownRow(item = item, totalExpense = uiState.totalExpense)
            }

            // Natural language query
            item {
                NlQueryCard(
                    query = uiState.nlQuery,
                    result = uiState.nlQueryResult,
                    onQueryChange = {}, // handled via viewmodel
                    onSubmit = onNlQuery
                )
            }
        }
    }
}

@Composable
private fun CategoryBreakdownRow(item: CategoryBreakdownItem, totalExpense: Double) {
    val pct = if (totalExpense > 0) (item.amount / totalExpense).toFloat() else 0f
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(item.categoryName, style = MaterialTheme.typography.bodyMedium)
            Text(
                "₹${String.format("%,.0f", item.amount)} (${(pct * 100).toInt()}%)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { pct.coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun NlQueryCard(
    query: String,
    result: String?,
    onQueryChange: (String) -> Unit,
    onSubmit: (String) -> Unit,
) {
    var localQuery by remember { mutableStateOf(query) }

    KharchaCard(modifier = Modifier.fillMaxWidth()) {
        Text("Ask a Question", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = localQuery,
            onValueChange = { localQuery = it },
            placeholder = { Text("e.g. How much did I spend on food this month?") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = false,
            minLines = 2
        )
        Spacer(Modifier.height(8.dp))
        Button(onClick = { onSubmit(localQuery) }, modifier = Modifier.fillMaxWidth()) {
            Text("Ask")
        }
        if (result != null) {
            Spacer(Modifier.height(8.dp))
            Text(result, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
