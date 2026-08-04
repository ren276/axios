package dev.sandesh.kharcha.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import dev.sandesh.kharcha.core.model.*

// ─── AccountEntity ───────────────────────────────────────────────────────────

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "bank_name") val bankName: String,
    @ColumnInfo(name = "account_last4") val accountLast4: String,
    @ColumnInfo(name = "type") val type: AccountType,
    @ColumnInfo(name = "current_balance") val currentBalance: Double,
    @ColumnInfo(name = "updated_at") val updatedAt: Long = System.currentTimeMillis()
)

fun AccountEntity.toDomain() = Account(id, bankName, accountLast4, type, currentBalance, updatedAt)
fun Account.toEntity() = AccountEntity(id, bankName, accountLast4, type, currentBalance, updatedAt)

// ─── TransactionEntity ───────────────────────────────────────────────────────

@Entity(
    tableName = "transactions",
    foreignKeys = [ForeignKey(
        entity = AccountEntity::class,
        parentColumns = ["id"],
        childColumns = ["account_id"],
        onDelete = ForeignKey.SET_NULL
    )],
    indices = [
        Index("account_id"),
        Index("timestamp"),
        Index("status"),
        Index("category_id"),
    ]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "account_id") val accountId: Long,
    @ColumnInfo(name = "amount") val amount: Double,
    @ColumnInfo(name = "type") val type: TransactionType,
    @ColumnInfo(name = "merchant_raw") val merchantRaw: String = "",
    @ColumnInfo(name = "merchant_normalized") val merchantNormalized: String = "",
    @ColumnInfo(name = "category_id") val categoryId: Long = CategoryIds.MISC,
    @ColumnInfo(name = "txn_ref") val txnRef: String = "",
    @ColumnInfo(name = "timestamp") val timestamp: Long,
    @ColumnInfo(name = "source") val source: TransactionSource,
    @ColumnInfo(name = "parse_confidence") val parseConfidence: Float = 1.0f,
    @ColumnInfo(name = "status") val status: TransactionStatus,
    @ColumnInfo(name = "is_duplicate_of") val isDuplicateOf: Long? = null,
    @ColumnInfo(name = "raw_sms_text") val rawSmsText: String? = null, // Purged after confirm
    @ColumnInfo(name = "note") val note: String = ""
)

fun TransactionEntity.toDomain(tags: List<String> = emptyList()) = Transaction(
    id, accountId, amount, type, merchantRaw, merchantNormalized,
    categoryId, txnRef, timestamp, source, parseConfidence, status,
    isDuplicateOf, tags, rawSmsText, note
)

fun Transaction.toEntity() = TransactionEntity(
    id, accountId, amount, type, merchantRaw, merchantNormalized,
    categoryId, txnRef, timestamp, source, parseConfidence, status,
    isDuplicateOf, rawSmsText, note
)

// ─── CategoryEntity ──────────────────────────────────────────────────────────

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "icon_ref") val iconRef: String,
    @ColumnInfo(name = "is_custom") val isCustom: Boolean = false
)

fun CategoryEntity.toDomain() = Category(id, name, iconRef, isCustom)
fun Category.toEntity() = CategoryEntity(id, name, iconRef, isCustom)

// ─── BudgetEntity ────────────────────────────────────────────────────────────

@Entity(
    tableName = "budgets",
    foreignKeys = [ForeignKey(
        entity = CategoryEntity::class,
        parentColumns = ["id"],
        childColumns = ["category_id"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("category_id")]
)
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "category_id") val categoryId: Long,
    @ColumnInfo(name = "amount") val amount: Double,
    @ColumnInfo(name = "period") val period: BudgetPeriod,
    @ColumnInfo(name = "start_date") val startDate: Long = System.currentTimeMillis()
)

fun BudgetEntity.toDomain() = Budget(id, categoryId, amount, period, startDate)
fun Budget.toEntity() = BudgetEntity(id, categoryId, amount, period, startDate)

// ─── BillEntity ──────────────────────────────────────────────────────────────

@Entity(tableName = "bills")
data class BillEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "due_date") val dueDate: Long,
    @ColumnInfo(name = "amount") val amount: Double?,
    @ColumnInfo(name = "is_recurring") val isRecurring: Boolean,
    @ColumnInfo(name = "recurrence_rule") val recurrenceRule: String? = null,
    @ColumnInfo(name = "linked_account_id") val linkedAccountId: Long? = null,
    @ColumnInfo(name = "is_paid") val isPaid: Boolean = false
)

fun BillEntity.toDomain() = Bill(id, name, dueDate, amount, isRecurring, recurrenceRule, linkedAccountId, isPaid)
fun Bill.toEntity() = BillEntity(id, name, dueDate, amount, isRecurring, recurrenceRule, linkedAccountId, isPaid)

// ─── TagEntity ───────────────────────────────────────────────────────────────

@Entity(tableName = "tags")
data class TagEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "color") val color: Long = 0xFF6200EE
)

fun TagEntity.toDomain() = Tag(id, name, color)
fun Tag.toEntity() = TagEntity(id, name, color)

// ─── TransactionTagCrossRef ──────────────────────────────────────────────────

@Entity(
    tableName = "transaction_tag_cross_ref",
    primaryKeys = ["transaction_id", "tag_id"],
    foreignKeys = [
        ForeignKey(AccountEntity::class, ["id"], ["transaction_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(TagEntity::class, ["id"], ["tag_id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class TransactionTagCrossRefEntity(
    @ColumnInfo(name = "transaction_id") val transactionId: Long,
    @ColumnInfo(name = "tag_id") val tagId: Long
)
