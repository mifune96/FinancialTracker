package com.aliimran.financialtracker.data.repository

import com.aliimran.financialtracker.data.local.dao.TransactionDao
import com.aliimran.financialtracker.data.mapper.toDomain
import com.aliimran.financialtracker.data.mapper.toEntity
import com.aliimran.financialtracker.domain.model.MonthlySummary
import com.aliimran.financialtracker.domain.model.Transaction
import com.aliimran.financialtracker.domain.model.TransactionType
import com.aliimran.financialtracker.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao,
) : TransactionRepository {

    override fun getTransactionsByMonth(year: Int, month: Int): Flow<List<Transaction>> =
        transactionDao.getTransactionsWithCategoryByMonth(year.toString(), month.toZeroPaddedMonth())
            .map { it.map { entity -> entity.toDomain() } }

    override fun getAllTransactions(): Flow<List<Transaction>> =
        transactionDao.getAllTransactionsWithCategory()
            .map { it.map { entity -> entity.toDomain() } }

    override suspend fun getTransactionById(id: Long): Transaction? =
        transactionDao.getTransactionWithCategoryById(id)?.toDomain()

    override suspend fun insertTransaction(transaction: Transaction): Long =
        transactionDao.insertTransaction(transaction.toEntity())

    override suspend fun updateTransaction(transaction: Transaction) =
        transactionDao.updateTransaction(transaction.toEntity())

    override suspend fun deleteTransaction(transaction: Transaction) =
        transactionDao.deleteTransaction(transaction.toEntity())

    override suspend fun deleteAllTransactions() =
        transactionDao.deleteAllTransactions()

    override fun getMonthlySummary(year: Int, month: Int): Flow<MonthlySummary> {
        val y = year.toString()
        val m = month.toZeroPaddedMonth()
        return combine(
            transactionDao.getTotalIncomeByMonth(y, m),
            transactionDao.getTotalExpenseByMonth(y, m),
        ) { income, expense ->
            MonthlySummary(month = month, year = year, totalIncome = income, totalExpense = expense)
        }
    }

    override fun getTransactionsByDateRange(
        type: TransactionType, startMs: Long, endMs: Long,
    ): Flow<List<Transaction>> =
        transactionDao.getTransactionsWithCategoryByDateRange(type.name, startMs, endMs)
            .map { it.map { entity -> entity.toDomain() } }

    /**
     * One-shot count of transactions linked to [categoryId].
     * Non-zero means the user should be warned before deleting the category.
     */
    override suspend fun getTransactionCountByCategory(categoryId: Long): Int =
        transactionDao.getTransactionCountByCategory(categoryId)

    private fun Int.toZeroPaddedMonth(): String = String.format(Locale.ROOT, "%02d", this)
}
