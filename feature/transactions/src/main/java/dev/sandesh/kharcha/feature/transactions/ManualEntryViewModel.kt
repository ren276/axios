package dev.sandesh.kharcha.feature.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sandesh.kharcha.core.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ManualEntryUiState(
    val type: TransactionType = TransactionType.DEBIT,
    val amount: String = "",
    val merchant: String = "",
    val note: String = "",
    val selectedCategory: Category? = null,
    val categories: List<Category> = emptyList(),
    val amountError: String? = null,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ManualEntryViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ManualEntryUiState())
    val uiState: StateFlow<ManualEntryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val cats = categoryRepository.getAllCategories()
            _uiState.value = _uiState.value.copy(
                categories = cats,
                selectedCategory = cats.firstOrNull()
            )
        }
    }

    fun setType(type: TransactionType) { _uiState.value = _uiState.value.copy(type = type) }
    fun setAmount(amount: String) { _uiState.value = _uiState.value.copy(amount = amount, amountError = null) }
    fun setMerchant(merchant: String) { _uiState.value = _uiState.value.copy(merchant = merchant) }
    fun setNote(note: String) { _uiState.value = _uiState.value.copy(note = note) }
    fun setCategory(category: Category) { _uiState.value = _uiState.value.copy(selectedCategory = category) }

    fun save() {
        val state = _uiState.value
        val amount = state.amount.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            _uiState.value = state.copy(amountError = "Please enter a valid amount")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            try {
                transactionRepository.insertTransaction(
                    Transaction(
                        accountId = 0L, // Manual entries have no linked account by default
                        amount = amount,
                        type = state.type,
                        merchantRaw = state.merchant,
                        merchantNormalized = state.merchant,
                        categoryId = state.selectedCategory?.id ?: CategoryIds.MISC,
                        timestamp = System.currentTimeMillis(),
                        source = TransactionSource.MANUAL,
                        parseConfidence = 1.0f,
                        status = TransactionStatus.MANUAL,
                        note = state.note
                    )
                )
                _uiState.value = _uiState.value.copy(isSaving = false, isSaved = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isSaving = false, error = e.message)
            }
        }
    }
}
