package dev.sandesh.kharcha.core.database.dao

import androidx.room.*
import dev.sandesh.kharcha.core.database.entity.*
import dev.sandesh.kharcha.core.model.TransactionStatus
import kotlinx.coroutines.flow.Flow

// ─── AccountDao ───────────────────────────────────────────────────────────────

@Dao
interface AccountDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(account: AccountEntity): Long

    @Update
    suspend fun update(account: AccountEntity)

    @Query("SELECT * FROM accounts WHERE id = :id")
    suspend fun getById(id: Long): AccountEntity?

    @Query("SELECT * FROM accounts WHERE bank_name = :bankName AND account_last4 = :last4 LIMIT 1")
    suspend fun getByBankAndLast4(bankName: String, last4: String): AccountEntity?

    @Query("SELECT * FROM accounts ORDER BY updated_at DESC")
    fun getAllFlow(): Flow<List<AccountEntity>>

    @Query("SELECT * FROM accounts ORDER BY updated_at DESC")
    suspend fun getAll(): List<AccountEntity>

    @Query("UPDATE accounts SET current_balance = :balance, updated_at = :updatedAt WHERE id = :id")
    suspend fun updateBalance(id: Long, balance: Double, updatedAt: Long = System.currentTimeMillis())
}

// ─── TransactionDao ───────────────────────────────────────────────────────────

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: TransactionEntity): Long

    @Update
    suspend fun update(transaction: TransactionEntity)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getById(id: Long): TransactionEntity?

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllFlow(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE status = :status ORDER BY timestamp DESC")
    fun getByStatusFlow(status: TransactionStatus): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE account_id = :accountId ORDER BY timestamp DESC")
    suspend fun getByAccount(accountId: Long): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE timestamp BETWEEN :from AND :to ORDER BY timestamp DESC")
    suspend fun getByDateRange(from: Long, to: Long): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE category_id = :categoryId ORDER BY timestamp DESC")
    suspend fun getByCategory(categoryId: Long): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE status = :status ORDER BY timestamp DESC")
    suspend fun getByStatus(status: TransactionStatus): List<TransactionEntity>

    @Query("""
        SELECT COALESCE(SUM(amount), 0.0) FROM transactions 
        WHERE category_id = :categoryId 
        AND type = 'DEBIT'
        AND timestamp BETWEEN :from AND :to
    """)
    suspend fun getTotalByCategory(categoryId: Long, from: Long, to: Long): Double

    @Query("""
        SELECT * FROM transactions 
        WHERE amount = :amount 
        AND account_id IN (SELECT id FROM accounts WHERE account_last4 = :accountLast4)
        AND timestamp BETWEEN :timestampFrom AND :timestampTo
        LIMIT 1
    """)
    suspend fun findDuplicate(
        amount: Double,
        accountLast4: String,
        timestampFrom: Long,
        timestampTo: Long
    ): TransactionEntity?

    @Query("UPDATE transactions SET raw_sms_text = NULL WHERE id = :id")
    suspend fun purgeRawSmsText(id: Long)

    @Query("UPDATE transactions SET raw_sms_text = NULL WHERE status = 'CONFIRMED' AND raw_sms_text IS NOT NULL")
    suspend fun purgeRawSmsForConfirmedTransactions(): Int

    @Query("DELETE FROM transactions")
    suspend fun deleteAll()

    @Query("""
        SELECT * FROM transactions 
        WHERE merchant_normalized = :merchant 
        ORDER BY timestamp DESC 
        LIMIT 6
    """)
    suspend fun getRecentByMerchant(merchant: String): List<TransactionEntity>

    @Query("""
        SELECT merchant_normalized, SUM(amount) as total 
        FROM transactions 
        WHERE type = 'DEBIT' AND timestamp BETWEEN :from AND :to
        GROUP BY merchant_normalized 
        ORDER BY total DESC 
        LIMIT :limit
    """)
    suspend fun getTopMerchants(from: Long, to: Long, limit: Int = 10): List<MerchantTotal>

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecent(limit: Int = 20): List<TransactionEntity>
}

data class MerchantTotal(
    val merchant_normalized: String,
    val total: Double
)

// ─── CategoryDao ──────────────────────────────────────────────────────────────

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(category: CategoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(categories: List<CategoryEntity>)

    @Query("SELECT * FROM categories ORDER BY id ASC")
    fun getAllFlow(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories ORDER BY id ASC")
    suspend fun getAll(): List<CategoryEntity>

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getById(id: Long): CategoryEntity?
}

// ─── BudgetDao ────────────────────────────────────────────────────────────────

@Dao
interface BudgetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(budget: BudgetEntity): Long

    @Update
    suspend fun update(budget: BudgetEntity)

    @Query("DELETE FROM budgets WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM budgets ORDER BY category_id ASC")
    fun getAllFlow(): Flow<List<BudgetEntity>>

    @Query("SELECT * FROM budgets ORDER BY category_id ASC")
    suspend fun getAll(): List<BudgetEntity>

    @Query("SELECT * FROM budgets WHERE category_id = :categoryId LIMIT 1")
    suspend fun getByCategory(categoryId: Long): BudgetEntity?
}

// ─── BillDao ─────────────────────────────────────────────────────────────────

@Dao
interface BillDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bill: BillEntity): Long

    @Update
    suspend fun update(bill: BillEntity)

    @Query("DELETE FROM bills WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM bills ORDER BY due_date ASC")
    fun getAllFlow(): Flow<List<BillEntity>>

    @Query("SELECT * FROM bills ORDER BY due_date ASC")
    suspend fun getAll(): List<BillEntity>

    @Query("""
        SELECT * FROM bills 
        WHERE due_date BETWEEN :now AND :cutoff
        ORDER BY due_date ASC
    """)
    suspend fun getUpcoming(now: Long, cutoff: Long): List<BillEntity>
}

// ─── TagDao ───────────────────────────────────────────────────────────────────

@Dao
interface TagDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(tag: TagEntity): Long

    @Query("SELECT * FROM tags ORDER BY name ASC")
    suspend fun getAll(): List<TagEntity>

    @Query("""
        SELECT t.* FROM tags t
        INNER JOIN transaction_tag_cross_ref x ON t.id = x.tag_id
        WHERE x.transaction_id = :transactionId
    """)
    suspend fun getTagsForTransaction(transactionId: Long): List<TagEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrossRef(crossRef: TransactionTagCrossRefEntity)

    @Query("DELETE FROM transaction_tag_cross_ref WHERE transaction_id = :transactionId")
    suspend fun deleteTagsForTransaction(transactionId: Long)
}
