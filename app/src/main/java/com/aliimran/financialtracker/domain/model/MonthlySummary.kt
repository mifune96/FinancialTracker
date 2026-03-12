package com.aliimran.financialtracker.domain.model

/**
 * Aggregated financial totals for a single calendar month.
 * [balance] is a derived, non-persisted property.
 */
data class MonthlySummary(
    val month: Int,   // 1-based: Jan = 1
    val year: Int,
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
) {
    /** Positive = surplus, negative = deficit. */
    val balance: Double get() = totalIncome - totalExpense
}
