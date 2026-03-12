package com.aliimran.financialtracker.domain.repository

import com.aliimran.financialtracker.domain.model.MonthlySummary
import com.aliimran.financialtracker.domain.model.Transaction
import com.aliimran.financialtracker.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {

    fun getTransactionsByMonth(year: Int, month: Int): Flow<List<Transaction>>
    fun getAllTransactions(): Flow<List<Transaction>>
    suspend fun getTransactionById(id: Long): Transaction?
    suspend fun insertTransaction(transaction: Transaction): Long
    suspend fun updateTransaction(transaction: Transaction)
    suspend fun deleteTransaction(transaction: Transaction)
    suspend fun deleteAllTransactions()
    fun getMonthlySummary(year: Int, month: Int): Flow<MonthlySummary>
    fun getTransactionsByDateRange(type: TransactionType, startMs: Long, endMs: Long): Flow<List<Transaction>>

    /**
     * Returns the number of transactions linked to [categoryId].
     * Used before category deletion to warn the user and confirm.
     * Room FK (ON DELETE SET DEFAULT) will reassign them to id=1 on actual delete.
     */
    suspend fun getTransactionCountByCategory(categoryId: Long): Int
}
