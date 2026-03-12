package com.aliimran.financialtracker.domain.usecase.analytics

import com.aliimran.financialtracker.domain.model.AnalyticsPeriod
import com.aliimran.financialtracker.domain.model.CategoryAnalytics
import com.aliimran.financialtracker.domain.model.PeriodSummary
import com.aliimran.financialtracker.domain.model.TransactionType
import com.aliimran.financialtracker.domain.repository.TransactionRepository
import com.aliimran.financialtracker.domain.usecase.base.FlowUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

/**
 * Calculates category-level spending/income analytics for a given
 * [AnalyticsPeriod] and [TransactionType].
 *
 * Pipeline:
 *   Room (reactive stream of raw transactions)
 *     → group by categoryId
 *     → sum amounts per group
 *     → calculate percentages + sweep angles
 *     → sort by totalAmount descending
 *     → wrap in [PeriodSummary]
 *
 * The result Flow re-emits automatically whenever the underlying
 * transaction data changes (e.g. a new transaction is inserted).
 */
class GetPeriodAnalyticsUseCase @Inject constructor(
    private val repository: TransactionRepository,
) : FlowUseCase<GetPeriodAnalyticsUseCase.Params, PeriodSummary>() {

    override fun execute(params: Params): Flow<PeriodSummary> {
        val range = params.period.toEpochMilliRange(params.referenceDate)

        return repository
            .getTransactionsByDateRange(
                type    = params.transactionType,
                startMs = range.first,
                endMs   = range.last,
            )
            .map { transactions ->

                // ── 1. Group by category ──────────────────────
                val grouped = transactions.groupBy { it.categoryId }

                // ── 2. Sum per category ───────────────────────
                val grandTotal = transactions.sumOf { it.amount }

                // ── 3. Build CategoryAnalytics list ──────────
                val breakdown: List<CategoryAnalytics> = grouped
                    .map { (_, txList) ->
                        val first      = txList.first()
                        val total      = txList.sumOf { it.amount }
                        val percentage = if (grandTotal > 0)
                            (total / grandTotal * 100).toFloat()
                        else 0f

                        CategoryAnalytics(
                            category = com.aliimran.financialtracker.domain.model.Category(
                                id          = first.categoryId,
                                name        = first.categoryName,
                                iconResName = first.categoryIconResName,
                                type        = params.transactionType,
                                color       = first.categoryColor,
                            ),
                            totalAmount      = total,
                            percentage       = percentage,
                            transactionCount = txList.size,
                            sweepAngle       = percentage * 3.6f,
                        )
                    }
                    .sortedByDescending { it.totalAmount }

                // ── 4. Wrap in PeriodSummary ──────────────────
                PeriodSummary(
                    period            = params.period,
                    transactionType   = params.transactionType,
                    grandTotal        = grandTotal,
                    categoryBreakdown = breakdown,
                    transactionCount  = transactions.size,
                )
            }
    }

    data class Params(
        val period          : AnalyticsPeriod,
        val transactionType : TransactionType  = TransactionType.EXPENSE,
        val referenceDate   : LocalDate        = LocalDate.now(),
    )
}
