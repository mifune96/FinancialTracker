package com.aliimran.financialtracker.domain.usecase.transaction

import com.aliimran.financialtracker.domain.model.MonthlySummary
import com.aliimran.financialtracker.domain.repository.TransactionRepository
import com.aliimran.financialtracker.domain.usecase.base.FlowUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Reactive income / expense / balance totals for a given month.
 * Re-emits on any DB change in that period.
 */
class GetMonthlySummaryUseCase @Inject constructor(
    private val repository: TransactionRepository,
) : FlowUseCase<GetMonthlySummaryUseCase.Params, MonthlySummary>() {

    override fun execute(params: Params): Flow<MonthlySummary> =
        repository.getMonthlySummary(params.year, params.month)

    data class Params(val year: Int, val month: Int)
}
