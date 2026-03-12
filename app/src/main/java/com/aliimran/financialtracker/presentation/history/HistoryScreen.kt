package com.aliimran.financialtracker.presentation.history

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aliimran.financialtracker.domain.model.GroupedTransactions
import com.aliimran.financialtracker.domain.model.Transaction
import com.aliimran.financialtracker.presentation.components.MonthYearPickerDialog
import com.aliimran.financialtracker.presentation.components.SummaryHeader
import com.aliimran.financialtracker.presentation.components.TransactionItem
import com.aliimran.financialtracker.util.DateFormatter.toFullDateString
import com.aliimran.financialtracker.util.CurrencyFormatter
import kotlinx.coroutines.flow.collectLatest

/**
 * History (Riwayat) screen — root composable.
 *
 * Observes [HistoryViewModel.uiState] via StateFlow, renders:
 *  - A TopAppBar with month/year navigation controls.
 *  - A [SummaryHeader] showing Income, Expense, and Balance totals.
 *  - A lazy grouped list of [TransactionItem]s, grouped by date.
 *  - A [MonthYearPickerDialog] when [HistoryUiState.isMonthPickerVisible] is true.
 *  - Loading and empty-state overlays.
 *
 * @param onTransactionClick  Navigates to the Transaction Detail screen.
 * @param viewModel           Provided by Hilt (default) or injected in tests.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onTransactionClick : (transactionId: Long) -> Unit,
    viewModel          : HistoryViewModel = hiltViewModel(),
) {
    val uiState          by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // ── Collect one-shot events ───────────────────────────────
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is HistoryViewModel.UiEvent.ShowSnackbar ->
                    snackbarHostState.showSnackbar(event.message)
                is HistoryViewModel.UiEvent.NavigateTo   -> { /* handled by NavGraph */ }
            }
        }
    }

    // ── Month/Year picker dialog ──────────────────────────────
    if (uiState.isMonthPickerVisible) {
        MonthYearPickerDialog(
            currentMonth = uiState.selectedMonth,
            currentYear  = uiState.selectedYear,
            onConfirm    = viewModel::onMonthYearSelected,
            onDismiss    = viewModel::onDismissMonthPicker,
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar       = {
            HistoryTopBar(
                month         = uiState.selectedMonth,
                year          = uiState.selectedYear,
                onPrevious    = viewModel::onPreviousMonth,
                onNext        = viewModel::onNextMonth,
                onPickerClick = viewModel::onToggleMonthPicker,
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            // ── Summary card ──────────────────────────────────
            SummaryHeader(summary = uiState.summary)

            // ── Transaction list / overlays ───────────────────
            Box(modifier = Modifier.fillMaxSize()) {

                when {
                    // Loading state
                    uiState.isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                        )
                    }

                    // Error state
                    uiState.errorMessage != null -> {
                        Text(
                            text      = uiState.errorMessage!!,
                            modifier  = Modifier
                                .align(Alignment.Center)
                                .padding(32.dp),
                            textAlign = TextAlign.Center,
                            color     = MaterialTheme.colorScheme.error,
                        )
                    }

                    // Empty state
                    uiState.groupedTransactions.isEmpty() -> {
                        EmptyTransactionsPlaceholder(
                            modifier = Modifier.align(Alignment.Center),
                        )
                    }

                    // Data state
                    else -> {
                        GroupedTransactionList(
                            groups             = uiState.groupedTransactions,
                            onTransactionClick = onTransactionClick,
                            onDeleteTransaction = viewModel::onDeleteTransaction,
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Top Bar
// ─────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HistoryTopBar(
    month         : Int,
    year          : Int,
    onPrevious    : () -> Unit,
    onNext        : () -> Unit,
    onPickerClick : () -> Unit,
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                modifier              = Modifier.fillMaxWidth(),
            ) {
                // ← Previous month
                IconButton(onClick = onPrevious) {
                    Icon(
                        imageVector        = Icons.Default.ChevronLeft,
                        contentDescription = "Bulan sebelumnya",
                    )
                }

                // Month/Year label — tappable to open picker
                TextButton(
                    onClick  = onPickerClick,
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        text       = com.aliimran.financialtracker.util.DateFormatter
                                        .monthDisplayName(month, year) + " $year",
                        style      = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color      = MaterialTheme.colorScheme.onSurface,
                        textAlign  = TextAlign.Center,
                    )
                }

                // Next month →
                IconButton(onClick = onNext) {
                    Icon(
                        imageVector        = Icons.Default.ChevronRight,
                        contentDescription = "Bulan berikutnya",
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        windowInsets = androidx.compose.foundation.layout.WindowInsets(0.dp),
    )
}

// ─────────────────────────────────────────────────────────────
// Grouped Transaction List
// ─────────────────────────────────────────────────────────────

@Composable
private fun GroupedTransactionList(
    groups              : List<GroupedTransactions>,
    onTransactionClick  : (Long) -> Unit,
    onDeleteTransaction : (Transaction) -> Unit,
    modifier            : Modifier = Modifier,
) {
    LazyColumn(
        modifier      = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp), // clear the FAB
    ) {
        groups.forEach { group ->

            // ── Date header ───────────────────────────────────
            item(key = "header_${group.date}") {
                DateGroupHeader(group = group)
            }

            // ── Transaction rows for this date ────────────────
            items(
                items = group.transactions,
                key   = { "tx_${it.id}" },
            ) { transaction ->
                TransactionItem(
                    transaction = transaction,
                    onClick     = { onTransactionClick(transaction.id) },
                )
                HorizontalDivider(
                    modifier    = Modifier.padding(horizontal = 16.dp),
                    color       = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                    thickness   = 0.5.dp,
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Date Group Header
// ─────────────────────────────────────────────────────────────

@Composable
private fun DateGroupHeader(
    group    : GroupedTransactions,
    modifier : Modifier = Modifier,
) {
    Row(
        modifier              = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically,
    ) {
        Text(
            text  = group.date.toFullDateString(),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold,
        )
        // Daily net total — positive green, negative red
        AnimatedVisibility(
            visible = group.dailyTotal != 0.0,
            enter   = fadeIn(),
            exit    = fadeOut(),
        ) {
            Text(
                text  = (if (group.dailyTotal >= 0) "+" else "") +
                        CurrencyFormatter.formatRupiah(group.dailyTotal),
                style = MaterialTheme.typography.labelMedium,
                color = if (group.dailyTotal >= 0)
                    com.aliimran.financialtracker.presentation.theme.IncomeGreen
                else
                    com.aliimran.financialtracker.presentation.theme.ExpenseRed,
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Empty State
// ─────────────────────────────────────────────────────────────

@Composable
private fun EmptyTransactionsPlaceholder(modifier: Modifier = Modifier) {
    Column(
        modifier              = modifier.padding(32.dp),
        horizontalAlignment   = Alignment.CenterHorizontally,
        verticalArrangement   = androidx.compose.foundation.layout.Arrangement.Center,
    ) {
        Text(
            text      = "📭",
            style     = MaterialTheme.typography.displayMedium,
        )
        Text(
            text      = "Belum ada transaksi",
            style     = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier  = Modifier.padding(top = 12.dp),
        )
        Text(
            text      = "Tap tombol + untuk menambahkan transaksi baru",
            style     = MaterialTheme.typography.bodySmall,
            color     = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier  = Modifier.padding(top = 4.dp),
        )
    }
}
