package com.aliimran.financialtracker.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aliimran.financialtracker.domain.model.GroupedTransactions
import com.aliimran.financialtracker.domain.model.Transaction
import com.aliimran.financialtracker.presentation.theme.ExpenseRed
import com.aliimran.financialtracker.presentation.theme.IncomeGreen
import com.aliimran.financialtracker.util.CurrencyFormatter
import com.aliimran.financialtracker.util.DateFormatter.toFullDateString

@Composable
fun GroupedTransactionList(
    groups              : List<GroupedTransactions>,
    onTransactionClick  : (Long) -> Unit,
    onDeleteTransaction : ((Transaction) -> Unit)? = null, // Optional if we want to support swipe-to-delete later
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
                    IncomeGreen
                else
                    ExpenseRed,
            )
        }
    }
}

@Composable
fun EmptyTransactionsPlaceholder(modifier: Modifier = Modifier, message: String = "Belum ada transaksi") {
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
            text      = message,
            style     = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier  = Modifier.padding(top = 12.dp),
            textAlign = TextAlign.Center,
        )
    }
}
