package dev.sandesh.kharcha.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sandesh.kharcha.core.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = true,
    val currentMonthLabel: String = "",
    val monthExpense: Double = 0.0,
    val monthIncome: Double = 0.0,
    val needsReviewCount: Int = 0,
    val recentTransactions: List<Transaction> = emptyList(),
    val budgetProgresses: List<BudgetProgress> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val budgetRepository: BudgetRepository,
    private val categoryRepository: CategoryRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeData()
    }

    private fun observeData() {
        viewModelScope.launch {
            combine(
                transactionRepository.getTransactionsFlow(),
                transactionRepository.getNeedsReviewFlow(),
                budgetRepository.getBudgetsFlow(),
            ) { transactions, needsReview, budgets ->
                val now = System.currentTimeMillis()
                val cal = Calendar.getInstance()
                val monthStart = cal.apply {
                    set(Calendar.DAY_OF_MONTH, 1)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis

                val monthTxns = transactions.filter { it.timestamp in monthStart..now }
                val monthExpense = monthTxns.filter { it.type == TransactionType.DEBIT }.sumOf { it.amount }
                val monthIncome = monthTxns.filter { it.type == TransactionType.CREDIT }.sumOf { it.amount }

                val monthLabel = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date(now))

                HomeUiState(
                    isLoading = false,
                    currentMonthLabel = monthLabel,
                    monthExpense = monthExpense,
                    monthIncome = monthIncome,
                    needsReviewCount = needsReview.size,
                    recentTransactions = transactions.take(10),
                    budgetProgresses = budgets.mapNotNull { budget ->
                        val spent = monthTxns
                            .filter { it.categoryId == budget.categoryId && it.type == TransactionType.DEBIT }
                            .sumOf { it.amount }
                        val category = categoryRepository.getCategoryById(budget.categoryId) ?: return@mapNotNull null
                        BudgetProgress(
                            budget = budget,
                            category = category,
                            spent = spent,
                            remaining = (budget.amount - spent).coerceAtLeast(0.0),
                            percentUsed = (spent / budget.amount).toFloat().coerceIn(0f, 2f),
                            projectedMonthEnd = null // Calculated by BudgetPacePredictor in Phase 6
                        )
                    }.sortedByDescending { it.percentUsed }
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
}
