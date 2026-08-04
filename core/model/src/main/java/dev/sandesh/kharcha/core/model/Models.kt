package dev.sandesh.kharcha.core.model

import kotlinx.serialization.Serializable

// ─── Enums ───────────────────────────────────────────────────────────────────

enum class TransactionType { DEBIT, CREDIT }

enum class TransactionSource { SMS, NOTIFICATION, MANUAL }

enum class TransactionStatus {
    CONFIRMED,      // Regex parser parsed with high confidence
    LLM_PARSED,     // On-device LLM parsed — surfaced in review inbox
    NEEDS_REVIEW,   // Below confidence threshold, awaiting user review
    MANUAL          // User-entered manually
}

enum class BudgetPeriod { MONTHLY, ROLLING }

enum class AccountType { BANK, CARD, WALLET }

// ─── Domain Models ────────────────────────────────────────────────────────────

@Serializable
data class Account(
    val id: Long = 0,
    val bankName: String,
    val accountLast4: String,         // Masked last 4 digits for display
    val type: AccountType,
    val currentBalance: Double,
    val updatedAt: Long = System.currentTimeMillis()
)

@Serializable
data class Category(
    val id: Long = 0,
    val name: String,
    val iconRef: String,              // Maps to static local icon resource name
    val isCustom: Boolean = false
)

@Serializable
data class Transaction(
    val id: Long = 0,
    val accountId: Long,
    val amount: Double,
    val type: TransactionType,
    val merchantRaw: String = "",     // Original merchant string from SMS
    val merchantNormalized: String = "", // "SWIGGY*BLR0234" → "Swiggy"
    val categoryId: Long = CategoryIds.MISC,
    val txnRef: String = "",          // Bank transaction reference if present
    val timestamp: Long,
    val source: TransactionSource,
    val parseConfidence: Float = 1.0f,
    val status: TransactionStatus,
    val isDuplicateOf: Long? = null,  // ID of the original if this is a dedupe candidate
    val tags: List<String> = emptyList(),
    val rawSmsText: String? = null,   // Ephemeral — purged after user confirms/corrects
    val note: String = ""
)

@Serializable
data class Budget(
    val id: Long = 0,
    val categoryId: Long,
    val amount: Double,
    val period: BudgetPeriod,
    val startDate: Long = System.currentTimeMillis()
)

@Serializable
data class Bill(
    val id: Long = 0,
    val name: String,
    val dueDate: Long,
    val amount: Double?,              // Null if amount varies (e.g. estimated)
    val isRecurring: Boolean,
    val recurrenceRule: String? = null, // ISO 8601 recurrence or simple "MONTHLY"
    val linkedAccountId: Long? = null,
    val isPaid: Boolean = false
)

@Serializable
data class Tag(
    val id: Long = 0,
    val name: String,
    val color: Long = 0xFF6200EE     // Default purple
)

@Serializable
data class TransactionTagCrossRef(
    val transactionId: Long,
    val tagId: Long
)

// ─── Parse Result (parser → engine contract) ────────────────────────────────

data class ParseResult(
    val confidence: Float,            // 0.0 to 1.0
    val transaction: Transaction?,
    val rawText: String,
    val parserName: String            // "SBI", "BOB", "LIC", "GENERIC", "LLM"
)

// ─── LLM Contracts ───────────────────────────────────────────────────────────

@Serializable
data class LlmExtractionResult(
    val amount: Double?,
    val type: TransactionType?,
    val merchantRaw: String?,
    val accountHint: String?,
    val timestamp: Long?,
    val confidence: Float = 0.7f,
    val isValid: Boolean = true,
    val errorMessage: String? = null
)

@Serializable
data class StructuredQuery(
    val categoryIds: List<Long>? = null,
    val accountIds: List<Long>? = null,
    val fromTimestamp: Long? = null,
    val toTimestamp: Long? = null,
    val aggregation: AggregationType,
    val transactionType: TransactionType? = null
)

enum class AggregationType { SUM, COUNT, AVERAGE, LIST }

@Serializable
data class MonthlySummary(
    val year: Int,
    val month: Int,
    val totalIncome: Double,
    val totalExpense: Double,
    val categoryBreakdown: Map<String, Double>, // categoryName → amount
    val topMerchants: List<Pair<String, Double>>,
    val vsLastMonthPercent: Double              // % change vs previous month
)

// ─── Budget Progress ──────────────────────────────────────────────────────────

data class BudgetProgress(
    val budget: Budget,
    val category: Category,
    val spent: Double,
    val remaining: Double,
    val percentUsed: Float,
    val projectedMonthEnd: Double?
) {
    val alertLevel: AlertLevel get() = when {
        percentUsed >= 1.0f -> AlertLevel.RED
        percentUsed >= 0.8f -> AlertLevel.AMBER
        percentUsed >= 0.5f -> AlertLevel.YELLOW
        else -> AlertLevel.GREEN
    }
}

enum class AlertLevel { GREEN, YELLOW, AMBER, RED }

// ─── Predefined Category IDs ─────────────────────────────────────────────────

object CategoryIds {
    const val FOOD = 1L
    const val TRAVEL = 2L
    const val SUBSCRIPTIONS = 3L
    const val RENT = 4L
    const val INSURANCE = 5L        // LIC / insurance
    const val FAMILY_TRANSFER = 6L
    const val UTILITIES = 7L
    const val SHOPPING = 8L
    const val MEDICAL = 9L
    const val MISC = 10L
}

// ─── User Preferences ─────────────────────────────────────────────────────────

data class UserPreferences(
    val isDarkMode: Boolean = false,
    val cloudLlmEnabled: Boolean = false,
    val isOnboardingComplete: Boolean = false
)

// ─── Repository interfaces ────────────────────────────────────────────────────

interface UserPreferencesRepository {
    val preferencesFlow: kotlinx.coroutines.flow.Flow<UserPreferences>
    suspend fun setDarkMode(isDark: Boolean)
    suspend fun setCloudLlmEnabled(enabled: Boolean)
    suspend fun setOnboardingCompleted(completed: Boolean)
}

interface DataExporter {
    suspend fun exportToCsv(transactions: List<Transaction>)
    suspend fun exportToPdf(transactions: List<Transaction>)
}

interface TransactionRepository {
    suspend fun insertTransaction(transaction: Transaction): Long
    suspend fun updateTransaction(transaction: Transaction)
    suspend fun deleteTransaction(id: Long)
    suspend fun getTransactionById(id: Long): Transaction?
    suspend fun getTransactionsByAccount(accountId: Long): List<Transaction>
    suspend fun getTransactionsByDateRange(from: Long, to: Long): List<Transaction>
    suspend fun getTransactionsByCategory(categoryId: Long): List<Transaction>
    suspend fun getTransactionsByStatus(status: TransactionStatus): List<Transaction>
    suspend fun getTransactionsByTag(tagName: String): List<Transaction>
    suspend fun getTotalByCategory(categoryId: Long, from: Long, to: Long): Double
    suspend fun checkDuplicate(amount: Double, accountLast4: String, timestampFrom: Long, timestampTo: Long): Transaction?
    suspend fun purgeRawSmsText(transactionId: Long)
    suspend fun purgeRawSmsForConfirmedTransactions(): Int
    suspend fun deleteAllTransactions()
    suspend fun queryTransactions(query: StructuredQuery): List<Transaction>
    fun getTransactionsFlow(): kotlinx.coroutines.flow.Flow<List<Transaction>>
    fun getNeedsReviewFlow(): kotlinx.coroutines.flow.Flow<List<Transaction>>
}

interface AccountRepository {
    suspend fun insertAccount(account: Account): Long
    suspend fun updateAccount(account: Account)
    suspend fun getAccountById(id: Long): Account?
    suspend fun getAccountByLast4(bankName: String, last4: String): Account?
    suspend fun getAllAccounts(): List<Account>
    suspend fun updateBalance(accountId: Long, balance: Double)
    fun getAccountsFlow(): kotlinx.coroutines.flow.Flow<List<Account>>
}

interface BudgetRepository {
    suspend fun insertBudget(budget: Budget): Long
    suspend fun updateBudget(budget: Budget)
    suspend fun deleteBudget(id: Long)
    suspend fun getBudgetByCategory(categoryId: Long): Budget?
    suspend fun getAllBudgets(): List<Budget>
    fun getBudgetsFlow(): kotlinx.coroutines.flow.Flow<List<Budget>>
}

interface BillRepository {
    suspend fun insertBill(bill: Bill): Long
    suspend fun updateBill(bill: Bill)
    suspend fun deleteBill(id: Long)
    suspend fun getAllBills(): List<Bill>
    suspend fun getUpcomingBills(daysAhead: Int): List<Bill>
    fun getBillsFlow(): kotlinx.coroutines.flow.Flow<List<Bill>>
}

interface CategoryRepository {
    suspend fun getAllCategories(): List<Category>
    suspend fun getCategoryById(id: Long): Category?
    suspend fun insertCategory(category: Category): Long
    fun getCategoriesFlow(): kotlinx.coroutines.flow.Flow<List<Category>>
}

interface TagRepository {
    suspend fun getAllTags(): List<Tag>
    suspend fun insertTag(tag: Tag): Long
    suspend fun getTagsForTransaction(transactionId: Long): List<Tag>
    suspend fun setTagsForTransaction(transactionId: Long, tagIds: List<Long>)
}
