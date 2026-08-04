package dev.sandesh.kharcha.feature.accounts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
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

@Composable
fun AccountsRoute(
    navController: NavController,
    viewModel: AccountsDashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    AccountsDashboardScreen(
        uiState = uiState,
        onAccountClick = { /* navigate to account detail */ },
        onBalanceOverride = viewModel::overrideBalance
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountsDashboardScreen(
    uiState: AccountsDashboardUiState,
    onAccountClick: (Long) -> Unit,
    onBalanceOverride: (Long, Double) -> Unit,
) {
    Scaffold(
        topBar = { KharchaTopBar(title = "Accounts") }
    ) { padding ->
        when {
            uiState.isLoading -> {
                Column(modifier = Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    repeat(3) { SkeletonLoader(modifier = Modifier.fillMaxWidth().height(90.dp)) }
                }
            }
            uiState.accounts.isEmpty() -> {
                EmptyState(
                    message = "No accounts tracked yet",
                    subtitle = "Accounts are auto-created as your bank SMS arrives",
                    icon = { Icon(Icons.Default.AccountBalance, null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary) },
                    modifier = Modifier.padding(padding)
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Net worth card
                    item {
                        KharchaCard(modifier = Modifier.fillMaxWidth()) {
                            Text("Total Balance", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "₹${String.format("%,.2f", uiState.netWorth)}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "${uiState.accounts.size} account(s) tracked",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    items(uiState.accounts, key = { it.id }) { account ->
                        AccountCard(
                            account = account,
                            onClick = { onAccountClick(account.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AccountCard(account: Account, onClick: () -> Unit) {
    KharchaCard(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.AccountBalance, null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(account.bankName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text(
                    "••••${account.accountLast4} · ${account.type.name}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "₹${String.format("%,.2f", account.currentBalance)}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text("Balance", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
