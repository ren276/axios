package dev.sandesh.kharcha.feature.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sandesh.kharcha.core.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class BudgetListUiState(
    val isLoading: Boolean = true,
    val budgetProgresses: List<BudgetProgress> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class BudgetListViewModel @Inject constructor(
    private val budgetRepository: BudgetRepository,
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(BudgetListUiState())
    val uiState: StateFlow<BudgetListUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                budgetRepository.getBudgetsFlow(),
                transactionRepository.getTransactionsFlow()
            ) { budgets, allTransactions ->
                val now = System.currentTimeMillis()
                val cal = Calendar.getInstance()
                val monthStart = cal.apply {
                    set(Calendar.DAY_OF_MONTH, 1)
                    set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
                }.timeInMillis

                val monthDebits = allTransactions.filter {
                    it.type == TransactionType.DEBIT && it.timestamp in monthStart..now
                }

                val progresses = budgets.mapNotNull { budget ->
                    val category = categoryRepository.getCategoryById(budget.categoryId) ?: return@mapNotNull null
                    val spent = monthDebits.filter { it.categoryId == budget.categoryId }.sumOf { it.amount }
                    BudgetProgress(
                        budget = budget,
                        category = category,
                        spent = spent,
                        remaining = (budget.amount - spent).coerceAtLeast(0.0),
                        percentUsed = if (budget.amount > 0) (spent / budget.amount).toFloat() else 0f,
                        projectedMonthEnd = null
                    )
                }.sortedByDescending { it.percentUsed }

                BudgetListUiState(isLoading = false, budgetProgresses = progresses)
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
}
