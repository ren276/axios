package dev.sandesh.kharcha.feature.accounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sandesh.kharcha.core.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AccountsDashboardUiState(
    val isLoading: Boolean = true,
    val accounts: List<Account> = emptyList(),
    val netWorth: Double = 0.0,
    val error: String? = null
)

@HiltViewModel
class AccountsDashboardViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AccountsDashboardUiState())
    val uiState: StateFlow<AccountsDashboardUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            accountRepository.getAccountsFlow().collect { accounts ->
                _uiState.value = AccountsDashboardUiState(
                    isLoading = false,
                    accounts = accounts,
                    netWorth = accounts.sumOf { it.currentBalance }
                )
            }
        }
    }

    fun overrideBalance(accountId: Long, newBalance: Double) {
        viewModelScope.launch {
            accountRepository.updateBalance(accountId, newBalance)
        }
    }
}
