package dev.sandesh.kharcha.feature.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sandesh.kharcha.core.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TransactionListUiState(
    val isLoading: Boolean = true,
    val transactions: List<Transaction> = emptyList(),
    val searchQuery: String = "",
    val activeFilter: TransactionFilter = TransactionFilter.ALL,
    val error: String? = null
)

@HiltViewModel
class TransactionListViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _filter = MutableStateFlow(TransactionFilter.ALL)

    private val _uiState = MutableStateFlow(TransactionListUiState())
    val uiState: StateFlow<TransactionListUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                transactionRepository.getTransactionsFlow(),
                _searchQuery,
                _filter
            ) { transactions, query, filter ->
                val filtered = transactions
                    .filter { txn ->
                        when (filter) {
                            TransactionFilter.ALL -> true
                            TransactionFilter.DEBIT -> txn.type == TransactionType.DEBIT
                            TransactionFilter.CREDIT -> txn.type == TransactionType.CREDIT
                            TransactionFilter.NEEDS_REVIEW ->
                                txn.status == TransactionStatus.NEEDS_REVIEW ||
                                txn.status == TransactionStatus.LLM_PARSED
                        }
                    }
                    .filter { txn ->
                        if (query.isEmpty()) true
                        else txn.merchantNormalized.contains(query, ignoreCase = true) ||
                                txn.merchantRaw.contains(query, ignoreCase = true) ||
                                txn.note.contains(query, ignoreCase = true)
                    }

                TransactionListUiState(
                    isLoading = false,
                    transactions = filtered,
                    searchQuery = query,
                    activeFilter = filter
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun setFilter(filter: TransactionFilter) { _filter.value = filter }
    fun setSearchQuery(query: String) { _searchQuery.value = query }
}
