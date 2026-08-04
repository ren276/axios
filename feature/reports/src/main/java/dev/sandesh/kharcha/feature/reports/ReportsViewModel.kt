package dev.sandesh.kharcha.feature.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sandesh.kharcha.core.model.*
import dev.sandesh.kharcha.feature.llm.LlmProvider
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

data class CategoryBreakdownItem(
    val categoryName: String,
    val amount: Double,
    val transactionCount: Int
)

data class MonthlySummaryUi(val narrative: String)

data class ReportsUiState(
    val isLoading: Boolean = true,
    val selectedMonthIndex: Int = 0,
    val availableMonths: List<String> = emptyList(),
    val availableMonthsData: List<Pair<Int, Int>> = emptyList(), // (month, year)
    val totalExpense: Double = 0.0,
    val totalIncome: Double = 0.0,
    val categoryBreakdown: List<CategoryBreakdownItem> = emptyList(),
    val monthlySummary: MonthlySummaryUi? = null,
    val nlQuery: String = "",
    val nlQueryResult: String? = null,
    val error: String? = null
)

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val llmProvider: LlmProvider
) : ViewModel() {

    private val cal = Calendar.getInstance()
    private val selectedMonth = MutableStateFlow(cal.get(Calendar.MONTH) + 1) // 1-indexed
    private val selectedYear = MutableStateFlow(cal.get(Calendar.YEAR))

    private val _uiState = MutableStateFlow(ReportsUiState())
    val uiState: StateFlow<ReportsUiState> = _uiState.asStateFlow()

    init {
        val months = buildAvailableMonths()
        _uiState.value = _uiState.value.copy(
            availableMonths = months.first,
            availableMonthsData = months.second
        )

        viewModelScope.launch {
            combine(
                transactionRepository.getTransactionsFlow(),
                selectedMonth,
                selectedYear
            ) { transactions, month, year ->
                val (start, end) = monthRange(month, year)
                val monthTxns = transactions.filter { it.timestamp in start..end }
                val debits = monthTxns.filter { it.type == TransactionType.DEBIT }
                val credits = monthTxns.filter { it.type == TransactionType.CREDIT }

                val allCats = categoryRepository.getAllCategories()
                val breakdown = allCats.mapNotNull { cat ->
                    val catDebits = debits.filter { it.categoryId == cat.id }
                    if (catDebits.isEmpty()) null
                    else CategoryBreakdownItem(
                        categoryName = cat.name,
                        amount = catDebits.sumOf { it.amount },
                        transactionCount = catDebits.size
                    )
                }.sortedByDescending { it.amount }

                val totalExpense = debits.sumOf { it.amount }
                val totalIncome = credits.sumOf { it.amount }

                // Build monthly summary for LLM
                val summaryData = MonthlySummary(
                    month = month,
                    year = year,
                    totalExpense = totalExpense,
                    totalIncome = totalIncome,
                    vsLastMonthPercent = 0.0, // TODO: compare vs previous month
                    topMerchants = emptyList(), // TODO: calculate top merchants
                    categoryBreakdown = breakdown.associate { it.categoryName to it.amount }
                )

                Triple(summaryData, breakdown, Pair(totalExpense, totalIncome))
            }.collect { (summaryData, breakdown, totals) ->
                val (totalExpense, totalIncome) = totals
                // Generate LLM summary asynchronously
                viewModelScope.launch {
                    val narrative = llmProvider.generateMonthlySummary(summaryData)
                    _uiState.value = _uiState.value.copy(
                        monthlySummary = MonthlySummaryUi(narrative = narrative)
                    )
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    totalExpense = totalExpense,
                    totalIncome = totalIncome,
                    categoryBreakdown = breakdown
                )
            }
        }
    }

    fun setMonth(month: Int, year: Int) {
        selectedMonth.value = month
        selectedYear.value = year
        val availableData = _uiState.value.availableMonthsData
        val newIndex = availableData.indexOfFirst { it.first == month && it.second == year }
        if (newIndex >= 0) _uiState.value = _uiState.value.copy(selectedMonthIndex = newIndex)
    }

    fun submitNlQuery(query: String) {
        viewModelScope.launch {
            try {
                val structuredQuery = llmProvider.naturalLanguageToQuery(query)
                // Execute the query against Room
                val transactions = transactionRepository.queryTransactions(structuredQuery)
                val result = when (structuredQuery.aggregation) {
                    AggregationType.SUM -> "Total: ₹${String.format("%,.2f", transactions.sumOf { it.amount })}"
                    AggregationType.COUNT -> "Count: ${transactions.size} transactions"
                    AggregationType.AVERAGE -> "Average: ₹${String.format("%,.2f", transactions.map { it.amount }.average())}"
                    AggregationType.LIST -> transactions.take(10).joinToString("\n") {
                        "${it.merchantNormalized}: ₹${String.format("%,.0f", it.amount)}"
                    }
                }
                _uiState.value = _uiState.value.copy(nlQueryResult = result)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(nlQueryResult = "Could not process query: ${e.message}")
            }
        }
    }

    private fun buildAvailableMonths(): Pair<List<String>, List<Pair<Int, Int>>> {
        val fmt = SimpleDateFormat("MMM yyyy", Locale.getDefault())
        val cal = Calendar.getInstance()
        val labels = mutableListOf<String>()
        val data = mutableListOf<Pair<Int, Int>>()
        // Last 6 months
        repeat(6) {
            labels.add(0, fmt.format(cal.time))
            data.add(0, Pair(cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR)))
            cal.add(Calendar.MONTH, -1)
        }
        return Pair(labels, data)
    }

    private fun monthRange(month: Int, year: Int): Pair<Long, Long> {
        val start = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        val end = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, 23); set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59); set(Calendar.MILLISECOND, 999)
        }.timeInMillis
        return Pair(start, end)
    }
}
