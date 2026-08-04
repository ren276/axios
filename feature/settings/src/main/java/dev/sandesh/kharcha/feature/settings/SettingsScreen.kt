package dev.sandesh.kharcha.feature.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dev.sandesh.kharcha.core.designsystem.components.KharchaTopBar

@Composable
fun SettingsRoute(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    SettingsScreen(
        uiState = uiState,
        onThemeChange = viewModel::setTheme,
        onCloudLlmToggle = viewModel::toggleCloudLlm,
        onBulkImport = viewModel::triggerBulkImport,
        onExportDataCsv = viewModel::exportDataCsv,
        onExportDataPdf = viewModel::exportDataPdf,
        onDeleteAllData = viewModel::deleteAllData,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onThemeChange: (Boolean) -> Unit,
    onCloudLlmToggle: (Boolean) -> Unit,
    onBulkImport: () -> Unit,
    onExportDataCsv: () -> Unit,
    onExportDataPdf: () -> Unit,
    onDeleteAllData: () -> Unit,
) {
    Scaffold(
        topBar = { KharchaTopBar(title = "Settings") }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            // Appearance
            item { SettingsGroupHeader("Appearance") }
            item {
                SettingsToggleRow(
                    icon = Icons.Default.DarkMode,
                    title = "Dark Mode",
                    subtitle = "Toggle dark/light theme",
                    checked = uiState.isDarkMode,
                    onCheckedChange = onThemeChange
                )
            }

            // Privacy & Security
            item { SettingsGroupHeader("Privacy & Security") }
            item {
                SettingsRow(
                    icon = Icons.Default.Lock,
                    title = "Biometric Lock",
                    subtitle = "App locked on cold start and after 30s background",
                    onClick = {}
                )
            }
            item {
                SettingsRow(
                    icon = Icons.Default.Shield,
                    title = "SMS Permission Status",
                    subtitle = if (uiState.hasSmsPermission) "✓ Active" else "⚠ Not granted — tap to fix",
                    onClick = {}
                )
            }
            item {
                SettingsRow(
                    icon = Icons.Default.NotificationsActive,
                    title = "Notification Access",
                    subtitle = if (uiState.hasNotificationAccess) "✓ Active" else "⚠ Not granted — tap to fix",
                    onClick = {}
                )
            }

            // LLM / AI
            item { SettingsGroupHeader("AI Features") }
            item {
                SettingsToggleRow(
                    icon = Icons.Default.Cloud,
                    title = "Cloud LLM Fallback",
                    subtitle = "Use Gemini Flash for complex categorization (no raw data sent)",
                    checked = uiState.cloudLlmEnabled,
                    onCheckedChange = onCloudLlmToggle
                )
            }

            // Data
            item { SettingsGroupHeader("Data Management") }
            item {
                SettingsRow(
                    icon = Icons.Default.Sync,
                    title = "Import Historical SMS",
                    subtitle = "Re-scan SMS inbox for past transactions",
                    onClick = onBulkImport
                )
            }
            item {
                SettingsRow(
                    icon = Icons.Default.FileDownload,
                    title = "Export Data (CSV)",
                    subtitle = "Download your transaction history as CSV",
                    onClick = onExportDataCsv
                )
            }
            item {
                SettingsRow(
                    icon = Icons.Default.PictureAsPdf,
                    title = "Export Report (PDF)",
                    subtitle = "Download a styled transaction report",
                    onClick = onExportDataPdf
                )
            }
            item {
                SettingsRow(
                    icon = Icons.Default.Delete,
                    title = "Delete All Data",
                    subtitle = "Permanently wipe database — this cannot be undone",
                    onClick = onDeleteAllData,
                    destructive = true
                )
            }

            // About
            item { SettingsGroupHeader("About") }
            item {
                SettingsRow(
                    icon = Icons.Default.Info,
                    title = "Version",
                    subtitle = "1.0.0-alpha (Phase 1)",
                    onClick = {}
                )
            }
        }
    }
}

@Composable
private fun SettingsGroupHeader(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
private fun SettingsRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    destructive: Boolean = false
) {
    ListItem(
        leadingContent = {
            Icon(
                icon, null,
                tint = if (destructive) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        headlineContent = {
            Text(
                title,
                color = if (destructive) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.onSurface
            )
        },
        supportingContent = { Text(subtitle, style = MaterialTheme.typography.bodySmall) },
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    )
}

@Composable
private fun SettingsToggleRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    ListItem(
        leadingContent = { Icon(icon, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
        headlineContent = { Text(title) },
        supportingContent = { Text(subtitle, style = MaterialTheme.typography.bodySmall) },
        trailingContent = {
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    )
}
