package com.aliimran.financialtracker.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aliimran.financialtracker.domain.model.GroupedTransactions
import com.aliimran.financialtracker.domain.model.Transaction
import com.aliimran.financialtracker.domain.usecase.transaction.DeleteTransactionUseCase
import com.aliimran.financialtracker.domain.usecase.transaction.GetMonthlySummaryUseCase
import com.aliimran.financialtracker.domain.usecase.transaction.GetTransactionsByMonthUseCase
import com.aliimran.financialtracker.util.DateFormatter.toLocalDate
import com.aliimran.financialtracker.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

/**
 * ViewModel for the History (Riwayat) screen.
 *
 * Responsibilities:
 *  1. Hold and expose [HistoryUiState] via [StateFlow].
 *  2. Combine the monthly transaction list with the monthly summary
 *     into a single cohesive state object.
 *  3. React to user events: month navigation, month picker, delete.
 *  4. Emit one-shot [UiEvent]s (e.g. snackbar messages) via [SharedFlow].
 *
 * The ViewModel does NOT directly depend on Room or Android framework types —
 * it only talks to use-case interfaces, keeping it fully unit-testable.
 */
@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getTransactionsByMonth : GetTransactionsByMonthUseCase,
    private val getMonthlySummary      : GetMonthlySummaryUseCase,
    private val deleteTransaction      : DeleteTransactionUseCase,
) : ViewModel() {

    // ── State ─────────────────────────────────────────────────

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    // ── One-shot events (snackbar, navigation) ────────────────

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    // ── Init ──────────────────────────────────────────────────

    init {
        // Immediately start observing the current month on construction.
        observeCurrentMonth()
    }

    // ── Private helpers ───────────────────────────────────────

    /**
     * Launches a coroutine that combines the two use-case Flows for the
     * currently selected month into a single [HistoryUiState] emission.
     *
     * Called whenever [selectedMonth] or [selectedYear] changes.
     * Any previously running collection job is cancelled implicitly by
     * the [viewModelScope] when [observeCurrentMonth] is called again.
     */
    private var observationJob: kotlinx.coroutines.Job? = null

    private fun observeCurrentMonth() {
        // Cancel the previous collection before starting a new one.
        observationJob?.cancel()

        val state  = _uiState.value
        val params = GetTransactionsByMonthUseCase.Params(state.selectedYear, state.selectedMonth)
        val summaryParams = GetMonthlySummaryUseCase.Params(state.selectedYear, state.selectedMonth)

        observationJob = viewModelScope.launch {
            combine(
                getTransactionsByMonth(params),
                getMonthlySummary(summaryParams),
            ) { transactionsResource, summaryResource ->
                Pair(transactionsResource, summaryResource)
            }.collect { (transactionsResource, summaryResource) ->

                _uiState.update { current ->
                    when {
                        // Both streams must succeed for a non-loading state.
                        transactionsResource is Resource.Loading ||
                        summaryResource      is Resource.Loading -> {
                            current.copy(isLoading = true, errorMessage = null)
                        }

                        transactionsResource is Resource.Error -> {
                            current.copy(
                                isLoading    = false,
                                errorMessage = transactionsResource.message,
                            )
                        }

                        summaryResource is Resource.Error -> {
                            current.copy(
                                isLoading    = false,
                                errorMessage = summaryResource.message,
                            )
                        }

                        transactionsResource is Resource.Success &&
                        summaryResource      is Resource.Success -> {
                            current.copy(
                                isLoading           = false,
                                errorMessage        = null,
                                summary             = summaryResource.data,
                                groupedTransactions = transactionsResource.data.groupByDate(),
                            )
                        }

                        else -> current
                    }
                }
            }
        }
    }

    /**
     * Groups a flat [List<Transaction>] into [List<GroupedTransactions>],
     * one bucket per calendar date, ordered newest-date-first.
     */
    private fun List<Transaction>.groupByDate(): List<GroupedTransactions> =
        groupBy { it.timestamp.toLocalDate() }
            .entries
            .sortedByDescending { it.key }
            .map { (date, txList) ->
                GroupedTransactions(
                    date         = date,
                    transactions = txList.sortedByDescending { it.timestamp },
                )
            }

    // ── Event handlers (called from the UI) ───────────────────

    /** Called when the user taps the left arrow to go to the previous month. */
    fun onPreviousMonth() {
        val current = _uiState.value
        val newDate = LocalDate.of(current.selectedYear, current.selectedMonth, 1)
            .minusMonths(1)
        _uiState.update {
            it.copy(
                selectedMonth = newDate.monthValue,
                selectedYear  = newDate.year,
                isLoading     = true,
            )
        }
        observeCurrentMonth()
    }

    /** Called when the user taps the right arrow to go to the next month. */
    fun onNextMonth() {
        val current = _uiState.value
        val newDate = LocalDate.of(current.selectedYear, current.selectedMonth, 1)
            .plusMonths(1)
        _uiState.update {
            it.copy(
                selectedMonth = newDate.monthValue,
                selectedYear  = newDate.year,
                isLoading     = true,
            )
        }
        observeCurrentMonth()
    }

    /** Called when the user selects a specific month/year from the picker. */
    fun onMonthYearSelected(month: Int, year: Int) {
        _uiState.update {
            it.copy(
                selectedMonth        = month,
                selectedYear         = year,
                isLoading            = true,
                isMonthPickerVisible = false,
            )
        }
        observeCurrentMonth()
    }

    /** Toggles the month/year picker dialog visibility. */
    fun onToggleMonthPicker() =
        _uiState.update { it.copy(isMonthPickerVisible = !it.isMonthPickerVisible) }

    /** Dismisses the month/year picker without changing selection. */
    fun onDismissMonthPicker() =
        _uiState.update { it.copy(isMonthPickerVisible = false) }

    /**
     * Deletes a transaction and emits a snackbar event to the UI.
     * Uses [viewModelScope] so the coroutine survives config changes.
     */
    fun onDeleteTransaction(transaction: com.aliimran.financialtracker.domain.model.Transaction) {
        viewModelScope.launch {
            when (val result = deleteTransaction(transaction)) {
                is Resource.Success -> {
                    _uiEvent.emit(UiEvent.ShowSnackbar("Transaksi berhasil dihapus"))
                }
                is Resource.Error -> {
                    _uiEvent.emit(UiEvent.ShowSnackbar("Gagal menghapus: ${result.message}"))
                }
                else -> Unit
            }
        }
    }

    // ── One-shot UI Events ────────────────────────────────────

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        data class NavigateTo(val route: String)     : UiEvent()
    }
}
