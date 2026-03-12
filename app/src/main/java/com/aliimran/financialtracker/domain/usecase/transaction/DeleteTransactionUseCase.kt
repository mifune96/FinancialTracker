package com.aliimran.financialtracker.domain.usecase.transaction

import com.aliimran.financialtracker.domain.model.Transaction
import com.aliimran.financialtracker.domain.repository.TransactionRepository
import com.aliimran.financialtracker.domain.usecase.base.SuspendUseCase
import javax.inject.Inject

/** Permanently removes a transaction from the data source. */
class DeleteTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository,
) : SuspendUseCase<Transaction, Unit>() {

    override suspend fun execute(params: Transaction) =
        repository.deleteTransaction(params)
}
