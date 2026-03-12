package com.aliimran.financialtracker.domain.usecase.transaction

import com.aliimran.financialtracker.domain.model.Transaction
import com.aliimran.financialtracker.domain.repository.TransactionRepository
import com.aliimran.financialtracker.domain.usecase.base.SuspendUseCase
import javax.inject.Inject

/**
 * Validates business rules and persists a new transaction.
 * Throws [IllegalArgumentException] if validation fails.
 */
class AddTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository,
) : SuspendUseCase<Transaction, Long>() {

    override suspend fun execute(params: Transaction): Long {
        require(params.amount > 0)     { "Transaction amount must be greater than zero." }
        require(params.categoryId > 0) { "A valid category must be selected." }
        return repository.insertTransaction(params)
    }
}
