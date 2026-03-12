package com.aliimran.financialtracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.aliimran.financialtracker.data.local.converter.Converters
import com.aliimran.financialtracker.data.local.dao.CategoryDao
import com.aliimran.financialtracker.data.local.dao.TransactionDao
import com.aliimran.financialtracker.data.local.entity.CategoryEntity
import com.aliimran.financialtracker.data.local.entity.TransactionEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Root Room database for Financial Tracker.
 *
 * VERSION HISTORY
 *   1 — Initial schema: categories + transactions tables.
 *
 * When incrementing [version], add a [androidx.room.migration.Migration]
 * in [DatabaseModule] and pass it to Room.databaseBuilder() to preserve
 * user data across app updates.
 */
@Database(
    entities = [
        CategoryEntity::class,
        TransactionEntity::class,
    ],
    version = 1,
    exportSchema = true,        // Exports schema JSON to schemas/ — commit to VCS!
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        const val DATABASE_NAME = "financial_tracker.db"

        /**
         * RoomDatabase.Callback that seeds default categories on first creation.
         * Injected via [DatabaseModule] so Hilt can provide the CoroutineScope.
         */
        fun buildCallback(scope: CoroutineScope): Callback = object : Callback() {

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                // Seed categories if the table is empty.
                scope.launch(Dispatchers.IO) {
                    val cursor = db.query("SELECT COUNT(*) FROM categories")
                    val count = if (cursor.moveToFirst()) cursor.getInt(0) else 0
                    cursor.close()
                    if (count == 0) {
                        db.execSQL(buildDefaultCategoriesSql())
                    }
                }
            }

            /**
             * Generates a single INSERT OR IGNORE statement for all default
             * categories so the operation is atomic and fast.
             *
             * Color values are Material 3 seed colors expressed as signed ARGB ints.
             * icon_res_name strings map to drawable resources in res/drawable/.
             */
            private fun buildDefaultCategoriesSql(): String {
                val rows = listOf(
                    // ── Expense categories ───────────────────
                    "('Makanan & Minuman','ic_cat_food',        'EXPENSE', ${0xFFE53935.toInt()})",
                    "('Transportasi',     'ic_cat_transport',   'EXPENSE', ${0xFF8E24AA.toInt()})",
                    "('Belanja',          'ic_cat_shopping',    'EXPENSE', ${0xFF00897B.toInt()})",
                    "('Hiburan',          'ic_cat_entertain',   'EXPENSE', ${0xFFD81B60.toInt()})",
                    "('Kesehatan',        'ic_cat_health',      'EXPENSE', ${0xFF039BE5.toInt()})",
                    "('Pendidikan',       'ic_cat_education',   'EXPENSE', ${0xFF7CB342.toInt()})",
                    "('Tagihan & Utilitas','ic_cat_bills',      'EXPENSE', ${0xFFF4511E.toInt()})",
                    "('Tempat Tinggal',   'ic_cat_housing',     'EXPENSE', ${0xFF3949AB.toInt()})",
                    "('Perjalanan',       'ic_cat_travel',      'EXPENSE', ${0xFF00ACC1.toInt()})",
                    "('Umum',             'ic_cat_general',     'EXPENSE', ${0xFF757575.toInt()})",
                    // ── Income categories ────────────────────
                    "('Gaji',             'ic_cat_salary',      'INCOME',  ${0xFF43A047.toInt()})",
                    "('Bisnis',           'ic_cat_business',    'INCOME',  ${0xFF1E88E5.toInt()})",
                    "('Freelance',        'ic_cat_freelance',   'INCOME',  ${0xFFFFB300.toInt()})",
                    "('Investasi',        'ic_cat_investment',  'INCOME',  ${0xFF6D4C41.toInt()})",
                    "('Hadiah',           'ic_cat_gift',        'INCOME',  ${0xFFEC407A.toInt()})",
                    "('Pendapatan Lain',  'ic_cat_other_income','INCOME',  ${0xFF26A69A.toInt()})",
                )
                val values = rows.joinToString(",\n  ")
                return """
                    INSERT OR IGNORE INTO categories (name, icon_res_name, type, color)
                    VALUES
                      $values
                """.trimIndent()
            }
        }
    }
}
