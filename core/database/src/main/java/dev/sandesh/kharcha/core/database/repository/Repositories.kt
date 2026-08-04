package dev.sandesh.kharcha.core.database.repository

import dev.sandesh.kharcha.core.database.dao.*
import dev.sandesh.kharcha.core.database.entity.*
import dev.sandesh.kharcha.core.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// ─── TransactionRepositoryImpl ───────────────────────────────────────────────

class TransactionRepositoryImpl(
    private val transactionDao: TransactionDao,
    private val tagDao: TagDao
) : TransactionRepository {

    override suspend fun insertTransaction(transaction: Transaction): Long =
        transactionDao.insert(transaction.toEntity())

    override suspend fun updateTransaction(transaction: Transaction) =
        transactionDao.update(transaction.toEntity())

    override suspend fun deleteTransaction(id: Long) = transactionDao.deleteById(id)

    override suspend fun getTransactionById(id: Long): Transaction? {
        val entity = transactionDao.getById(id) ?: return null
        val tags = tagDao.getTagsForTransaction(id).map { it.name }
        return entity.toDomain(tags)
    }

    override suspend fun getTransactionsByAccount(accountId: Long): List<Transaction> =
        transactionDao.getByAccount(accountId).map { it.toDomain() }

    override suspend fun getTransactionsByDateRange(from: Long, to: Long): List<Transaction> =
        transactionDao.getByDateRange(from, to).map { it.toDomain() }

    override suspend fun getTransactionsByCategory(categoryId: Long): List<Transaction> =
        transactionDao.getByCategory(categoryId).map { it.toDomain() }

    override suspend fun getTransactionsByStatus(status: TransactionStatus): List<Transaction> =
        transactionDao.getByStatus(status).map { it.toDomain() }

    override suspend fun getTransactionsByTag(tagName: String): List<Transaction> {
        // Tags join is handled at DAO level — for now, filter in memory
        // (Optimize with a proper join query in v1.1 if volume is large)
        val all = transactionDao.getByDateRange(0, Long.MAX_VALUE)
        return all.filter { entity ->
            val tags = tagDao.getTagsForTransaction(entity.id).map { it.name }
            tags.any { it.equals(tagName, ignoreCase = true) }
        }.map { it.toDomain() }
    }

    override suspend fun getTotalByCategory(categoryId: Long, from: Long, to: Long): Double =
        transactionDao.getTotalByCategory(categoryId, from, to)

    override suspend fun checkDuplicate(
        amount: Double, accountLast4: String, timestampFrom: Long, timestampTo: Long
    ): Transaction? = transactionDao.findDuplicate(amount, accountLast4, timestampFrom, timestampTo)
        ?.toDomain()

    override suspend fun purgeRawSmsText(transactionId: Long) =
        transactionDao.purgeRawSmsText(transactionId)

    override suspend fun purgeRawSmsForConfirmedTransactions(): Int =
        transactionDao.purgeRawSmsForConfirmedTransactions()

    override suspend fun deleteAllTransactions() =
        transactionDao.deleteAll()

    override suspend fun queryTransactions(query: StructuredQuery): List<Transaction> {
        val all = transactionDao.getByDateRange(0, Long.MAX_VALUE).map { it.toDomain() }
        // Simple in-memory filtering logic for Phase 1
        return all
    }

    override fun getTransactionsFlow(): Flow<List<Transaction>> =
        transactionDao.getAllFlow().map { list -> list.map { it.toDomain() } }

    override fun getNeedsReviewFlow(): Flow<List<Transaction>> =
        transactionDao.getByStatusFlow(TransactionStatus.NEEDS_REVIEW)
            .map { list -> list.map { it.toDomain() } }
}

// ─── AccountRepositoryImpl ────────────────────────────────────────────────────

class AccountRepositoryImpl(private val dao: AccountDao) : AccountRepository {
    override suspend fun insertAccount(account: Account): Long = dao.insert(account.toEntity())
    override suspend fun updateAccount(account: Account) = dao.update(account.toEntity())
    override suspend fun getAccountById(id: Long): Account? = dao.getById(id)?.toDomain()
    override suspend fun getAccountByLast4(bankName: String, last4: String): Account? =
        dao.getByBankAndLast4(bankName, last4)?.toDomain()
    override suspend fun getAllAccounts(): List<Account> = dao.getAll().map { it.toDomain() }
    override suspend fun updateBalance(accountId: Long, balance: Double) =
        dao.updateBalance(accountId, balance)
    override fun getAccountsFlow(): Flow<List<Account>> =
        dao.getAllFlow().map { list -> list.map { it.toDomain() } }
}

// ─── BudgetRepositoryImpl ─────────────────────────────────────────────────────

class BudgetRepositoryImpl(private val dao: BudgetDao) : BudgetRepository {
    override suspend fun insertBudget(budget: Budget): Long = dao.insert(budget.toEntity())
    override suspend fun updateBudget(budget: Budget) = dao.update(budget.toEntity())
    override suspend fun deleteBudget(id: Long) = dao.deleteById(id)
    override suspend fun getBudgetByCategory(categoryId: Long): Budget? =
        dao.getByCategory(categoryId)?.toDomain()
    override suspend fun getAllBudgets(): List<Budget> = dao.getAll().map { it.toDomain() }
    override fun getBudgetsFlow(): Flow<List<Budget>> =
        dao.getAllFlow().map { list -> list.map { it.toDomain() } }
}

// ─── BillRepositoryImpl ───────────────────────────────────────────────────────

class BillRepositoryImpl(private val dao: BillDao) : BillRepository {
    override suspend fun insertBill(bill: Bill): Long = dao.insert(bill.toEntity())
    override suspend fun updateBill(bill: Bill) = dao.update(bill.toEntity())
    override suspend fun deleteBill(id: Long) = dao.deleteById(id)
    override suspend fun getAllBills(): List<Bill> = dao.getAll().map { it.toDomain() }
    override suspend fun getUpcomingBills(daysAhead: Int): List<Bill> {
        val now = System.currentTimeMillis()
        val cutoff = now + (daysAhead * 24 * 60 * 60 * 1000L)
        return dao.getUpcoming(now, cutoff).map { it.toDomain() }
    }
    override fun getBillsFlow(): Flow<List<Bill>> =
        dao.getAllFlow().map { list -> list.map { it.toDomain() } }
}

// ─── CategoryRepositoryImpl ───────────────────────────────────────────────────

class CategoryRepositoryImpl(private val dao: CategoryDao) : CategoryRepository {
    override suspend fun getAllCategories(): List<Category> = dao.getAll().map { it.toDomain() }
    override suspend fun getCategoryById(id: Long): Category? = dao.getById(id)?.toDomain()
    override suspend fun insertCategory(category: Category): Long = dao.insert(category.toEntity())
    override fun getCategoriesFlow(): Flow<List<Category>> =
        dao.getAllFlow().map { list -> list.map { it.toDomain() } }
}

// ─── TagRepositoryImpl ────────────────────────────────────────────────────────

class TagRepositoryImpl(private val dao: TagDao) : TagRepository {
    override suspend fun getAllTags(): List<Tag> = dao.getAll().map { it.toDomain() }
    override suspend fun insertTag(tag: Tag): Long = dao.insert(tag.toEntity())
    override suspend fun getTagsForTransaction(transactionId: Long): List<Tag> =
        dao.getTagsForTransaction(transactionId).map { it.toDomain() }
    override suspend fun setTagsForTransaction(transactionId: Long, tagIds: List<Long>) {
        dao.deleteTagsForTransaction(transactionId)
        tagIds.forEach { tagId ->
            dao.insertCrossRef(TransactionTagCrossRefEntity(transactionId, tagId))
        }
    }
}
