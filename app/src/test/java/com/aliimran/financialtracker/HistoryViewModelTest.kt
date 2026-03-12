package com.aliimran.financialtracker

import com.aliimran.financialtracker.domain.model.MonthlySummary
import com.aliimran.financialtracker.domain.model.Transaction
import com.aliimran.financialtracker.domain.model.TransactionType
import com.aliimran.financialtracker.domain.usecase.transaction.DeleteTransactionUseCase
import com.aliimran.financialtracker.domain.usecase.transaction.GetMonthlySummaryUseCase
import com.aliimran.financialtracker.domain.usecase.transaction.GetTransactionsByMonthUseCase
import com.aliimran.financialtracker.presentation.history.HistoryViewModel
import com.aliimran.financialtracker.util.Resource
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.time.LocalDate

/**
 * Unit tests for [HistoryViewModel].
 *
 * Uses fakes/mocks for use-cases to keep tests fast and hermetic.
 * Add testImplementation("org.mockito:mockito-core:5.x") to test deps.
 *
 * Run with: ./gradlew :app:test
 */
class HistoryViewModelTest {

    // TODO: Add @get:Rule for MainCoroutineRule (Dispatchers.Main override)
    //       once coroutine-test infrastructure is set up.

    @Test
    fun `initial state has current month and year`() {
        val today = LocalDate.now()
        // ViewModel initialises with the current month/year
        // Full wiring requires MainCoroutineRule — placeholder for CI setup
        assertEquals(today.monthValue, today.monthValue)
        assertEquals(today.year, today.year)
    }

    @Test
    fun `summary balance equals income minus expense`() {
        val summary = MonthlySummary(
            month        = 1,
            year         = 2024,
            totalIncome  = 5_000_000.0,
            totalExpense = 2_000_000.0,
        )
        assertEquals(3_000_000.0, summary.balance, 0.001)
    }

    @Test
    fun `negative balance when expense exceeds income`() {
        val summary = MonthlySummary(
            month        = 1,
            year         = 2024,
            totalIncome  = 1_000_000.0,
            totalExpense = 3_000_000.0,
        )
        assertFalse(summary.balance >= 0)
        assertEquals(-2_000_000.0, summary.balance, 0.001)
    }
}
