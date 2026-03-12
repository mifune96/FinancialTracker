package com.aliimran.financialtracker.presentation.analytics

import com.aliimran.financialtracker.domain.model.AnalyticsPeriod
import com.aliimran.financialtracker.domain.model.CategoryAnalytics
import com.aliimran.financialtracker.domain.model.PeriodSummary
import com.aliimran.financialtracker.domain.model.TransactionType
import java.time.LocalDate

/**
 * Immutable state snapshot for the Analytics (Grafik) screen.
 *
 * @param selectedPeriod         WEEK / MONTH / YEAR tab.
 * @param selectedType           EXPENSE or INCOME chart toggle.
 * @param periodSummary          Fully-calculated breakdown from the use case.
 * @param selectedCategoryIndex  Index of the tapped donut segment (null = none).
 * @param isLoading              True while the first data fetch is in-flight.
 * @param errorMessage           Non-null on repository error.
 * @param referenceDate          Anchor date for period calculation (today).
 */
data class AnalyticsUiState(
    val selectedPeriod        : AnalyticsPeriod = AnalyticsPeriod.MONTH,
    val selectedType          : TransactionType  = TransactionType.EXPENSE,
    val periodSummary         : PeriodSummary?   = null,
    val selectedCategoryIndex : Int?             = null,
    val isLoading             : Boolean          = true,
    val errorMessage          : String?          = null,
    val referenceDate         : LocalDate        = LocalDate.now(),
) {
    /** The highlighted [CategoryAnalytics], or null when no segment selected. */
    val selectedCategory: CategoryAnalytics?
        get() = selectedCategoryIndex?.let {
            periodSummary?.categoryBreakdown?.getOrNull(it)
        }

    /** Display label for the date-range sub-header. */
    val periodRangeLabel: String
        get() = selectedPeriod.toDisplayRange(referenceDate)

    /** Convenience: breakdown list or empty list when no data. */
    val categoryBreakdown: List<CategoryAnalytics>
        get() = periodSummary?.categoryBreakdown ?: emptyList()

    val grandTotal: Double
        get() = periodSummary?.grandTotal ?: 0.0

    val isEmpty: Boolean
        get() = periodSummary?.isEmpty ?: true
}
