package com.aliimran.financialtracker.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.aliimran.financialtracker.data.local.entity.TransactionEntity
import com.aliimran.financialtracker.data.local.entity.TransactionWithCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    // ── Monthly queries (History screen) ──────────────────────

    @Transaction
    @Query("""
        SELECT * FROM transactions
        WHERE strftime('%Y', datetime(timestamp / 1000, 'unixepoch')) = :year
          AND strftime('%m', datetime(timestamp / 1000, 'unixepoch')) = :month
        ORDER BY timestamp DESC
    """)
    fun getTransactionsWithCategoryByMonth(
        year: String,
        month: String,
    ): Flow<List<TransactionWithCategory>>

    @Transaction
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactionsWithCategory(): Flow<List<TransactionWithCategory>>

    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    suspend fun getTransactionById(id: Long): TransactionEntity?

    @androidx.room.Transaction
    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    suspend fun getTransactionWithCategoryById(id: Long): TransactionWithCategory?

    // ── Date-range queries (Analytics screen) ─────────────────

    @Transaction
    @Query("""
        SELECT * FROM transactions
        WHERE type     = :type
          AND timestamp >= :startMs
          AND timestamp  < :endMs
        ORDER BY timestamp DESC
    """)
    fun getTransactionsWithCategoryByDateRange(
        type    : String,
        startMs : Long,
        endMs   : Long,
    ): Flow<List<TransactionWithCategory>>

    // ── Category-linked queries (Category Management) ─────────

    /**
     * Returns the number of transactions linked to [categoryId].
     * Used to warn the user before deleting a category that is in use.
     * Room's ON DELETE SET DEFAULT will move them to category_id = 1
     * on actual deletion, but showing the count is good UX.
     */
    @Query("SELECT COUNT(*) FROM transactions WHERE category_id = :categoryId")
    suspend fun getTransactionCountByCategory(categoryId: Long): Int

    // ── Monthly aggregate queries (History screen summary) ────

    @Query("""
        SELECT COALESCE(SUM(amount), 0.0) FROM transactions
        WHERE type = 'INCOME'
          AND strftime('%Y', datetime(timestamp / 1000, 'unixepoch')) = :year
          AND strftime('%m', datetime(timestamp / 1000, 'unixepoch')) = :month
    """)
    fun getTotalIncomeByMonth(year: String, month: String): Flow<Double>

    @Query("""
        SELECT COALESCE(SUM(amount), 0.0) FROM transactions
        WHERE type = 'EXPENSE'
          AND strftime('%Y', datetime(timestamp / 1000, 'unixepoch')) = :year
          AND strftime('%m', datetime(timestamp / 1000, 'unixepoch')) = :month
    """)
    fun getTotalExpenseByMonth(year: String, month: String): Flow<Double>

    // ── Write operations ──────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertTransaction(entity: TransactionEntity): Long

    @Update
    suspend fun updateTransaction(entity: TransactionEntity)

    @Query("DELETE FROM transactions")
    suspend fun deleteAllTransactions()

    @Delete
    suspend fun deleteTransaction(entity: TransactionEntity)
}
