package dev.sandesh.kharcha.core.database.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.sandesh.kharcha.core.database.*
import dev.sandesh.kharcha.core.database.dao.*
import dev.sandesh.kharcha.core.database.repository.*
import dev.sandesh.kharcha.core.model.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): KharchaDatabase {
        val passphrase = DatabaseKeyManager.getPassphrase(context)
        return KharchaDatabase.create(context, passphrase)
    }

    @Provides fun provideAccountDao(db: KharchaDatabase): AccountDao = db.accountDao()
    @Provides fun provideTransactionDao(db: KharchaDatabase): TransactionDao = db.transactionDao()
    @Provides fun provideCategoryDao(db: KharchaDatabase): CategoryDao = db.categoryDao()
    @Provides fun provideBudgetDao(db: KharchaDatabase): BudgetDao = db.budgetDao()
    @Provides fun provideBillDao(db: KharchaDatabase): BillDao = db.billDao()
    @Provides fun provideTagDao(db: KharchaDatabase): TagDao = db.tagDao()

    @Provides
    @Singleton
    fun provideTransactionRepository(dao: TransactionDao, tagDao: TagDao): TransactionRepository =
        TransactionRepositoryImpl(dao, tagDao)

    @Provides
    @Singleton
    fun provideAccountRepository(dao: AccountDao): AccountRepository =
        AccountRepositoryImpl(dao)

    @Provides
    @Singleton
    fun provideBudgetRepository(dao: BudgetDao): BudgetRepository =
        BudgetRepositoryImpl(dao)

    @Provides
    @Singleton
    fun provideBillRepository(dao: BillDao): BillRepository =
        BillRepositoryImpl(dao)

    @Provides
    @Singleton
    fun provideCategoryRepository(dao: CategoryDao): CategoryRepository =
        CategoryRepositoryImpl(dao)

    @Provides
    @Singleton
    fun provideTagRepository(dao: TagDao): TagRepository =
        TagRepositoryImpl(dao)
}
