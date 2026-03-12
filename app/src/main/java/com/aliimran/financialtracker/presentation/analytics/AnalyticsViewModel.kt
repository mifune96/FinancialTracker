package com.aliimran.financialtracker.presentation.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aliimran.financialtracker.domain.model.AnalyticsPeriod
import com.aliimran.financialtracker.domain.model.TransactionType
import com.aliimran.financialtracker.domain.usecase.analytics.GetPeriodAnalyticsUseCase
import com.aliimran.financialtracker.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Analytics (Grafik) screen.
 *
 * Responsibilities:
 *  1. Hold the current [AnalyticsUiState] as a [StateFlow].
 *  2. React to user-driven changes: period tab, type toggle, segment tap.
 *  3. Launch & cancel [GetPeriodAnalyticsUseCase] whenever inputs change,
 *     using [flatMapLatest]-style cancellation via Job replacement.
 *
 * The ViewModel uses a single [loadAnalytics] function that cancels any
 * in-flight collection before starting a new one — avoiding duplicate
 * or stale emissions when the user switches tabs rapidly.
 */
@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val getPeriodAnalytics: GetPeriodAnalyticsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    /** Tracks the active analytics collection job for cancellation. */
    private var analyticsJob: Job? = null

    init {
        // Load the default view: EXPENSE for current MONTH
        loadAnalytics()
    }

    // ── User Events ───────────────────────────────────────────

    /**
     * Called when the user taps a period tab (Pekan / Bulan / Tahun).
     * Resets segment selection and triggers a fresh data load.
     */
    fun onPeriodSelected(period: AnalyticsPeriod) {
        if (_uiState.value.selectedPeriod == period) return
        _uiState.update {
            it.copy(
                selectedPeriod        = period,
                selectedCategoryIndex = null,
                isLoading             = true,
            )
        }
        loadAnalytics()
    }

    /**
     * Called when the user toggles between Pengeluaran / Pemasukan.
     * Resets segment selection and triggers a fresh data load.
     */
    fun onTypeSelected(type: TransactionType) {
        if (_uiState.value.selectedType == type) return
        _uiState.update {
            it.copy(
                selectedType          = type,
                selectedCategoryIndex = null,
                isLoading             = true,
            )
        }
        loadAnalytics()
    }

    /**
     * Called when the user taps a segment on the Donut Chart or
     * a row in the category list.
     *
     * If the same segment is tapped again, deselects it (toggle behaviour).
     */
    fun onSegmentSelected(index: Int) {
        _uiState.update { state ->
            val newIndex = if (state.selectedCategoryIndex == index) null else index
            state.copy(selectedCategoryIndex = newIndex)
        }
    }

    /** Clears segment selection (e.g. on period/type change or background tap). */
    fun onClearSelection() =
        _uiState.update { it.copy(selectedCategoryIndex = null) }

    // ── Data Loading ──────────────────────────────────────────

    /**
     * Cancels any running analytics collection, then starts a new coroutine
     * that collects the [GetPeriodAnalyticsUseCase] Flow and maps the result
     * into [AnalyticsUiState].
     *
     * This achieves `flatMapLatest`-style behaviour without needing a
     * combined Flow, keeping the cancellation logic explicit and readable.
     */
    private fun loadAnalytics() {
        analyticsJob?.cancel()
        analyticsJob = viewModelScope.launch {
            val state = _uiState.value
            val params = GetPeriodAnalyticsUseCase.Params(
                period          = state.selectedPeriod,
                transactionType = state.selectedType,
                referenceDate   = state.referenceDate,
            )

            getPeriodAnalytics(params).collect { resource ->
                _uiState.update { current ->
                    when (resource) {
                        Resource.Loading          -> current.copy(isLoading = true, errorMessage = null)
                        is Resource.Success       -> current.copy(
                            isLoading     = false,
                            errorMessage  = null,
                            periodSummary = resource.data,
                            // Reset selection if the breakdown changed (e.g. period switched)
                            selectedCategoryIndex = current.selectedCategoryIndex?.takeIf { idx ->
                                idx < resource.data.categoryBreakdown.size
                            },
                        )
                        is Resource.Error         -> current.copy(
                            isLoading    = false,
                            errorMessage = resource.message,
                        )
                    }
                }
            }
        }
    }
}
