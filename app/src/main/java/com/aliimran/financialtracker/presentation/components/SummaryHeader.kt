package com.aliimran.financialtracker.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aliimran.financialtracker.domain.model.MonthlySummary
import com.aliimran.financialtracker.presentation.theme.ExpenseRed
import com.aliimran.financialtracker.presentation.theme.IncomeGreen
import com.aliimran.financialtracker.util.CurrencyFormatter

/**
 * Summary card displayed at the top of the History screen.
 * Shows Total Pemasukan (Income), Total Pengeluaran (Expense), and Saldo (Balance)
 * for the currently selected month.
 *
 * @param summary   Aggregated monthly totals from the ViewModel state.
 * @param modifier  Optional Modifier for external layout control.
 */
@Composable
fun SummaryHeader(
    summary: MonthlySummary,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier  = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape     = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors    = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // ── Income / Expense row ──────────────────────────
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                SummaryItem(
                    label  = "Pemasukan",
                    amount = summary.totalIncome,
                    color  = IncomeGreen,
                )
                SummaryItem(
                    label     = "Pengeluaran",
                    amount    = summary.totalExpense,
                    color     = ExpenseRed,
                    textAlign = androidx.compose.ui.text.style.TextAlign.End,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(12.dp))

            // ── Balance row ───────────────────────────────────
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically,
            ) {
                Text(
                    text  = "Saldo",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text       = CurrencyFormatter.formatRupiah(summary.balance),
                    style      = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color      = if (summary.balance >= 0) IncomeGreen else ExpenseRed,
                )
            }
        }
    }
}

@Composable
private fun SummaryItem(
    label     : String,
    amount    : Double,
    color     : androidx.compose.ui.graphics.Color,
    textAlign : androidx.compose.ui.text.style.TextAlign = androidx.compose.ui.text.style.TextAlign.Start,
    modifier  : Modifier = Modifier,
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.Start) {
        Text(
            text      = label,
            style     = MaterialTheme.typography.labelMedium,
            color     = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = textAlign,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text       = CurrencyFormatter.formatRupiah(amount),
            style      = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color      = color,
            fontSize   = 15.sp,
            textAlign  = textAlign,
        )
    }
}
