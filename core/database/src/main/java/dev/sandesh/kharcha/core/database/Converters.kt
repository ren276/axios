package dev.sandesh.kharcha.core.database

import android.content.Context
import androidx.room.TypeConverter
import dev.sandesh.kharcha.core.model.*

class Converters {
    @TypeConverter fun fromTransactionType(value: TransactionType): String = value.name
    @TypeConverter fun toTransactionType(value: String): TransactionType = TransactionType.valueOf(value)

    @TypeConverter fun fromTransactionSource(value: TransactionSource): String = value.name
    @TypeConverter fun toTransactionSource(value: String): TransactionSource = TransactionSource.valueOf(value)

    @TypeConverter fun fromTransactionStatus(value: TransactionStatus): String = value.name
    @TypeConverter fun toTransactionStatus(value: String): TransactionStatus = TransactionStatus.valueOf(value)

    @TypeConverter fun fromBudgetPeriod(value: BudgetPeriod): String = value.name
    @TypeConverter fun toBudgetPeriod(value: String): BudgetPeriod = BudgetPeriod.valueOf(value)

    @TypeConverter fun fromAccountType(value: AccountType): String = value.name
    @TypeConverter fun toAccountType(value: String): AccountType = AccountType.valueOf(value)
}
