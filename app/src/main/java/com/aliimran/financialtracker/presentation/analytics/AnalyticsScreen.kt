package com.aliimran.financialtracker.presentation.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aliimran.financialtracker.domain.model.AnalyticsPeriod
import com.aliimran.financialtracker.domain.model.TransactionType
import com.aliimran.financialtracker.presentation.components.CategorySpendingRow
import com.aliimran.financialtracker.presentation.components.DonutChart
import com.aliimran.financialtracker.presentation.theme.ExpenseRed
import com.aliimran.financialtracker.presentation.theme.IncomeGreen
import com.aliimran.financialtracker.util.CurrencyFormatter

/**
 * Analytics (Grafik) screen — visualises spending/income per category.
 *
 * Screen anatomy (top → bottom):
 * ┌─────────────────────────────────────────┐
 * │              Grafik                     │ TopAppBar
 * ├─────────────────────────────────────────┤
 * │ [Pengeluaran]        [Pemasukan]        │ Type toggle row
 * ├─────────────────────────────────────────┤
 * │ [  Pekan  ][  Bulan  ][  Tahun  ]       │ Period TabRow
 * │  1 Jan 2024 – 31 Jan 2024               │ Date range subtitle
 * ├─────────────────────────────────────────┤
 * │                                         │
 * │           Rp 1.500.000  ← center        │
 * │         ╔═══════════╗                   │ Donut Chart (Canvas)
 * │      ╔══╝           ╚══╗                │
 * │      ╚═════════════════╝                │
 * │                                         │
 * ├─────────────────────────────────────────┤
 * │  ● Makanan      35%  ████  Rp 525.000   │
 * │  ● Transport    20%  ███   Rp 300.000   │ Category Spending List
 * │  ● Belanja      15%  ██    Rp 225.000   │ (LazyColumn, scrollable)
 * │  …                                      │
 * └─────────────────────────────────────────┘
 *
 * @param viewModel Hilt-provided [AnalyticsViewModel].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text       = "Grafik",
                        fontWeight = FontWeight.Bold,
                        fontSize   = 20.sp,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
                windowInsets = androidx.compose.foundation.layout.WindowInsets(0.dp),
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->

        LazyColumn(
            modifier       = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 80.dp),
        ) {

            // ── Type toggle (Pengeluaran / Pemasukan) ──────────
            item {
                TransactionTypeToggle(
                    selectedType = uiState.selectedType,
                    onTypeSelected = viewModel::onTypeSelected,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }

            // ── Period tabs (Pekan / Bulan / Tahun) ───────────
            item {
                PeriodTabRow(
                    selectedPeriod = uiState.selectedPeriod,
                    onPeriodSelected = viewModel::onPeriodSelected,
                )
                // Date range subtitle
                Text(
                    text      = uiState.periodRangeLabel,
                    style     = MaterialTheme.typography.labelSmall,
                    color     = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier  = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    textAlign = TextAlign.Center,
                )
            }

            // ── Loading / Error / Empty state ──────────────────
            when {
                uiState.isLoading -> item {
                    Box(
                        modifier         = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }
                uiState.errorMessage != null -> item {
                    ErrorState(message = uiState.errorMessage!!)
                }
                uiState.isEmpty -> item {
                    EmptyAnalyticsState(
                        period = uiState.selectedPeriod,
                        type   = uiState.selectedType,
                    )
                }
                else -> {
                    // ── Donut Chart ────────────────────────────
                    item {
                        Box(
                            modifier         = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            DonutChart(
                                segments        = uiState.categoryBreakdown,
                                grandTotal      = uiState.grandTotal,
                                selectedIndex   = uiState.selectedCategoryIndex,
                                onSegmentTapped = viewModel::onSegmentSelected,
                                chartSize       = 260.dp,
                                strokeWidth     = 48.dp,
                            )
                        }
                    }

                    // ── Grand Total summary card ───────────────
                    item {
                        AnalyticsSummaryCard(
                            grandTotal       = uiState.grandTotal,
                            transactionCount = uiState.periodSummary?.transactionCount ?: 0,
                            selectedType     = uiState.selectedType,
                            modifier         = Modifier.padding(horizontal = 16.dp),
                        )
                        Spacer(Modifier.height(8.dp))
                    }

                    // ── Section header ─────────────────────────
                    item {
                        Text(
                            text       = "Rincian Kategori",
                            style      = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            modifier   = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        )
                    }

                    // ── Category breakdown list ────────────────
                    itemsIndexed(
                        items = uiState.categoryBreakdown,
                        key   = { _, item -> item.category.id },
                    ) { index, analytics ->
                        CategorySpendingRow(
                            analytics  = analytics,
                            isSelected = uiState.selectedCategoryIndex == index,
                            onClick    = { viewModel.onSegmentSelected(index) },
                            rank       = index + 1,
                            modifier   = Modifier.padding(horizontal = 8.dp),
                        )
                        if (index < uiState.categoryBreakdown.lastIndex) {
                            HorizontalDivider(
                                modifier  = Modifier.padding(horizontal = 24.dp),
                                color     = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                                thickness = 0.5.dp,
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Type Toggle (Pengeluaran / Pemasukan)
// ─────────────────────────────────────────────────────────────

@Composable
private fun TransactionTypeToggle(
    selectedType   : TransactionType,
    onTypeSelected : (TransactionType) -> Unit,
    modifier       : Modifier = Modifier,
) {
    Row(
        modifier              = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        listOf(
            TransactionType.EXPENSE to "Pengeluaran",
            TransactionType.INCOME  to "Pemasukan",
        ).forEach { (type, label) ->
            val isSelected = selectedType == type
            val color      = if (type == TransactionType.EXPENSE) ExpenseRed else IncomeGreen

            Text(
                text       = label,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color      = if (isSelected) color
                             else MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign  = TextAlign.Center,
                modifier   = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        color = if (isSelected) color.copy(alpha = 0.12f)
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    )
                    .clickable { onTypeSelected(type) }
                    .padding(vertical = 10.dp),
            )
        }
    }
}

// Period Tab Row
// ─────────────────────────────────────────────────────────────

@Composable
private fun PeriodTabRow(
    selectedPeriod   : AnalyticsPeriod,
    onPeriodSelected : (AnalyticsPeriod) -> Unit,
) {
    val periods      = AnalyticsPeriod.entries
    val selectedIndex = periods.indexOf(selectedPeriod)

    TabRow(
        selectedTabIndex = selectedIndex,
        containerColor   = MaterialTheme.colorScheme.surface,
        contentColor     = MaterialTheme.colorScheme.primary,
        indicator        = { tabPositions ->
            SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                color    = MaterialTheme.colorScheme.primary,
            )
        },
    ) {
        periods.forEachIndexed { index, period ->
            Tab(
                selected = index == selectedIndex,
                onClick  = { onPeriodSelected(period) },
                text     = {
                    Text(
                        text       = period.label,
                        fontWeight = if (index == selectedIndex) FontWeight.Bold
                                     else FontWeight.Normal,
                    )
                },
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Summary Card
// ─────────────────────────────────────────────────────────────

@Composable
private fun AnalyticsSummaryCard(
    grandTotal       : Double,
    transactionCount : Int,
    selectedType     : TransactionType,
    modifier         : Modifier = Modifier,
) {
    val accentColor = if (selectedType == TransactionType.EXPENSE) ExpenseRed else IncomeGreen

    Row(
        modifier              = modifier
            .fillMaxWidth()
            .background(
                color = accentColor.copy(alpha = 0.08f),
                shape = RoundedCornerShape(12.dp),
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text  = if (selectedType == TransactionType.EXPENSE)
                        "Total Pengeluaran" else "Total Pemasukan",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text       = CurrencyFormatter.formatRupiah(grandTotal),
                style      = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color      = accentColor,
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text  = "Transaksi",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text       = transactionCount.toString(),
                style      = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color      = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Empty & Error States
// ─────────────────────────────────────────────────────────────

@Composable
private fun EmptyAnalyticsState(
    period   : AnalyticsPeriod,
    type     : TransactionType,
    modifier : Modifier = Modifier,
) {
    Column(
        modifier            = modifier
            .fillMaxWidth()
            .height(300.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = "📊", fontSize = 52.sp)
        Spacer(Modifier.height(12.dp))
        Text(
            text       = "Belum ada data",
            style      = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text      = "Tidak ada ${if (type == TransactionType.EXPENSE) "pengeluaran" else "pemasukan"} " +
                        "di ${period.label.lowercase()} ini",
            style     = MaterialTheme.typography.bodySmall,
            color     = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier  = Modifier.padding(horizontal = 32.dp),
        )
    }
}

@Composable
private fun ErrorState(message: String, modifier: Modifier = Modifier) {
    Box(
        modifier         = modifier
            .fillMaxWidth()
            .height(300.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text      = message,
            color     = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
            modifier  = Modifier.padding(32.dp),
        )
    }
}
