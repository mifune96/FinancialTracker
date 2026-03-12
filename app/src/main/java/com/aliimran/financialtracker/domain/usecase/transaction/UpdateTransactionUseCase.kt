package com.aliimran.financialtracker.domain.usecase.transaction

import com.aliimran.financialtracker.domain.model.Transaction
import com.aliimran.financialtracker.domain.repository.TransactionRepository
import com.aliimran.financialtracker.domain.usecase.base.SuspendUseCase
import javax.inject.Inject

/** Updates an existing transaction entry after validation. */
class UpdateTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository,
) : SuspendUseCase<Transaction, Unit>() {

    override suspend fun execute(params: Transaction) {
        require(params.id > 0)         { "Cannot update a transaction without a valid id." }
        require(params.amount > 0)     { "Transaction amount must be greater than zero." }
        repository.updateTransaction(params)
    }
}
