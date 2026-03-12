package com.aliimran.financialtracker.presentation.history

import com.aliimran.financialtracker.domain.model.GroupedTransactions
import com.aliimran.financialtracker.domain.model.MonthlySummary
import java.time.LocalDate

/**
 * Immutable UI state for the History (Riwayat) screen.
 *
 * A single data class is used instead of a sealed class because the
 * screen can simultaneously show a summary header AND a loading spinner
 * for the list — states are not mutually exclusive.
 *
 * Driven entirely by [HistoryViewModel] via StateFlow.
 *
 * @param selectedMonth      Currently displayed month (1-based, Jan = 1).
 * @param selectedYear       Currently displayed year.
 * @param summary            Aggregated totals for the selected period.
 * @param groupedTransactions Transactions bucketed by [LocalDate], newest-first.
 * @param isLoading          True while the initial data fetch is in flight.
 * @param errorMessage       Non-null when a repository error occurred.
 * @param isMonthPickerVisible Controls visibility of the month/year picker dialog.
 */
data class HistoryUiState(
    val selectedMonth: Int             = LocalDate.now().monthValue,
    val selectedYear: Int              = LocalDate.now().year,
    val summary: MonthlySummary        = MonthlySummary(
                                            month = LocalDate.now().monthValue,
                                            year  = LocalDate.now().year,
                                        ),
    val groupedTransactions: List<GroupedTransactions> = emptyList(),
    val isLoading: Boolean             = true,
    val errorMessage: String?          = null,
    val isMonthPickerVisible: Boolean  = false,
)
