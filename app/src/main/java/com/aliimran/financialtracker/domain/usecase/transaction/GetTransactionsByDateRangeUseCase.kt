package com.aliimran.financialtracker.domain.usecase.transaction

import com.aliimran.financialtracker.domain.model.Transaction
import com.aliimran.financialtracker.domain.model.TransactionType
import com.aliimran.financialtracker.domain.repository.TransactionRepository
import com.aliimran.financialtracker.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetTransactionsByDateRangeUseCase @Inject constructor(
    private val repository: TransactionRepository,
) {
    data class Params(
        val type: TransactionType?,
        val startMs: Long,
        val endMs: Long,
    )

    operator fun invoke(params: Params): Flow<Resource<List<Transaction>>> =
        repository.getTransactionsByDateRange(params.type, params.startMs, params.endMs)
            .map { Resource.Success(it) as Resource<List<Transaction>> }
            .catch { emit(Resource.Error(it.message ?: "An unexpected error occurred")) }
}
