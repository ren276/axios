package dev.sandesh.kharcha.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sandesh.kharcha.core.model.*
import dev.sandesh.kharcha.feature.ingestion.SmsBulkImportWorker
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.first

data class SettingsUiState(
    val isDarkMode: Boolean = false,
    val cloudLlmEnabled: Boolean = false,
    val hasSmsPermission: Boolean = false,
    val hasNotificationAccess: Boolean = false,
    val importProgress: ImportState? = null,
    val error: String? = null
)

sealed class ImportState {
    object Running : ImportState()
    data class Done(val processed: Int, val imported: Int) : ImportState()
    data class Error(val message: String) : ImportState()
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val transactionRepository: TransactionRepository,
    private val workManager: WorkManager,
    private val dataExporter: DataExporter
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            userPreferencesRepository.preferencesFlow.collect { prefs ->
                _uiState.value = _uiState.value.copy(
                    isDarkMode = prefs.isDarkMode,
                    cloudLlmEnabled = prefs.cloudLlmEnabled
                )
            }
        }
    }

    fun setTheme(isDark: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setDarkMode(isDark)
        }
    }

    fun toggleCloudLlm(enabled: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setCloudLlmEnabled(enabled)
        }
    }

    fun triggerBulkImport() {
        _uiState.value = _uiState.value.copy(importProgress = ImportState.Running)
        val request = OneTimeWorkRequestBuilder<SmsBulkImportWorker>().build()
        workManager.enqueue(request)
        // In a real app we would observe the WorkInfo. For now, assume it runs in the background.
        _uiState.value = _uiState.value.copy(
            importProgress = ImportState.Done(processed = -1, imported = -1) // Indeterminate
        )
    }

    fun exportDataCsv() {
        viewModelScope.launch {
            val txns = transactionRepository.getTransactionsFlow().first()
            dataExporter.exportToCsv(txns)
        }
    }

    fun exportDataPdf() {
        viewModelScope.launch {
            val txns = transactionRepository.getTransactionsFlow().first()
            dataExporter.exportToPdf(txns)
        }
    }

    fun deleteAllData() {
        viewModelScope.launch {
            transactionRepository.deleteAllTransactions()
        }
    }
}
