package com.aliimran.financialtracker.domain.model

/**
 * Complete analytics snapshot for a single [AnalyticsPeriod].
 *
 * @param period             The selected time window.
 * @param transactionType    EXPENSE or INCOME — what the chart is showing.
 * @param grandTotal         Sum of all amounts across every category.
 * @param categoryBreakdown  Per-category analytics, sorted by [CategoryAnalytics.totalAmount] desc.
 * @param transactionCount   Total number of transactions in this period.
 */
data class PeriodSummary(
    val period            : AnalyticsPeriod,
    val transactionType   : TransactionType,
    val grandTotal        : Double,
    val categoryBreakdown : List<CategoryAnalytics>,
    val transactionCount  : Int,
) {
    val isEmpty: Boolean get() = categoryBreakdown.isEmpty() || grandTotal == 0.0
}
