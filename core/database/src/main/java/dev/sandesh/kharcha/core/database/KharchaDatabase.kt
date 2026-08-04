package dev.sandesh.kharcha.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dev.sandesh.kharcha.core.database.dao.*
import dev.sandesh.kharcha.core.database.entity.*
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

/**
 * Kharcha Room database — SQLCipher encrypted.
 *
 * Version history:
 *  1 → Initial schema (v1.0)
 *
 * IMPORTANT: Never downgrade the DB version. Always add migrations, never
 * use fallbackToDestructiveMigration() in production — that would wipe user data.
 */
@Database(
    entities = [
        AccountEntity::class,
        TransactionEntity::class,
        CategoryEntity::class,
        BudgetEntity::class,
        BillEntity::class,
        TagEntity::class,
        TransactionTagCrossRefEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class KharchaDatabase : RoomDatabase() {

    abstract fun accountDao(): AccountDao
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun budgetDao(): BudgetDao
    abstract fun billDao(): BillDao
    abstract fun tagDao(): TagDao

    companion object {
        const val DB_NAME = "kharcha_v1.db"

        fun create(context: Context, passphrase: CharArray): KharchaDatabase {
            val factory = SupportFactory(SQLiteDatabase.getBytes(passphrase))

            return Room.databaseBuilder(
                context.applicationContext,
                KharchaDatabase::class.java,
                DB_NAME
            )
                .openHelperFactory(factory)
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Pre-populate default categories
                        DefaultCategories.all.forEach { cat ->
                            db.execSQL(
                                "INSERT OR IGNORE INTO categories (id, name, icon_ref, is_custom) VALUES (?, ?, ?, 0)",
                                arrayOf(cat.id, cat.name, cat.iconRef)
                            )
                        }
                    }
                })
                .build()
        }
    }
}

// ─── Default categories (pre-populated on first run) ─────────────────────────

object DefaultCategories {
    val all = listOf(
        CategoryEntity(id = 1, name = "Food & Dining",      iconRef = "ic_category_food",        isCustom = false),
        CategoryEntity(id = 2, name = "Travel",             iconRef = "ic_category_travel",       isCustom = false),
        CategoryEntity(id = 3, name = "Subscriptions",      iconRef = "ic_category_subscription", isCustom = false),
        CategoryEntity(id = 4, name = "Rent",               iconRef = "ic_category_rent",         isCustom = false),
        CategoryEntity(id = 5, name = "Insurance / LIC",    iconRef = "ic_category_insurance",    isCustom = false),
        CategoryEntity(id = 6, name = "Family Transfer",    iconRef = "ic_category_family",       isCustom = false),
        CategoryEntity(id = 7, name = "Utilities",          iconRef = "ic_category_utilities",    isCustom = false),
        CategoryEntity(id = 8, name = "Shopping",           iconRef = "ic_category_shopping",     isCustom = false),
        CategoryEntity(id = 9, name = "Medical",            iconRef = "ic_category_medical",      isCustom = false),
        CategoryEntity(id = 10, name = "Miscellaneous",     iconRef = "ic_category_misc",         isCustom = false),
    )
}
