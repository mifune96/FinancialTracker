package com.aliimran.financialtracker.domain.usecase.transaction

import com.aliimran.financialtracker.domain.model.Transaction
import com.aliimran.financialtracker.domain.repository.TransactionRepository
import com.aliimran.financialtracker.domain.usecase.base.FlowUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Retrieves all transactions for a specific calendar month as a reactive stream.
 * Automatically re-emits when the underlying data changes.
 */
class GetTransactionsByMonthUseCase @Inject constructor(
    private val repository: TransactionRepository,
) : FlowUseCase<GetTransactionsByMonthUseCase.Params, List<Transaction>>() {

    override fun execute(params: Params): Flow<List<Transaction>> =
        repository.getTransactionsByMonth(params.year, params.month)

    data class Params(val year: Int, val month: Int)
}
