package com.aliimran.financialtracker.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import com.aliimran.financialtracker.util.IconMapper
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.aliimran.financialtracker.domain.model.Transaction
import com.aliimran.financialtracker.domain.model.TransactionType
import com.aliimran.financialtracker.presentation.theme.ExpenseRed
import com.aliimran.financialtracker.presentation.theme.IncomeGreen
import com.aliimran.financialtracker.presentation.theme.TransferBlue
import com.aliimran.financialtracker.util.CurrencyFormatter
import com.aliimran.financialtracker.util.Extensions.toComposeColor

/**
 * A single row in the transaction list.
 * Displays the category icon, name, optional note, and the signed amount.
 *
 * @param transaction  Domain model to display.
 * @param onClick      Called when the user taps the row (navigate to detail).
 * @param modifier     Optional external modifier.
 */
@Composable
fun TransactionItem(
    transaction : Transaction,
    onClick     : () -> Unit,
    modifier    : Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // ── Category icon chip ────────────────────────────────
        CategoryIconChip(
            color        = transaction.categoryColor.toComposeColor(),
            iconResName  = transaction.categoryIconResName,
            contentDescription = transaction.categoryName,
        )

        Spacer(modifier = Modifier.width(12.dp))

        // ── Category name + note ──────────────────────────────
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = transaction.categoryName.ifBlank { "Umum" },
                style      = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines   = 1,
                overflow   = TextOverflow.Ellipsis,
            )
            if (transaction.note.isNotBlank()) {
                Text(
                    text     = transaction.note,
                    style    = MaterialTheme.typography.bodySmall,
                    color    = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // ── Signed amount ─────────────────────────────────────
        val (prefix, amountColor) = when (transaction.type) {
            TransactionType.INCOME   -> "+" to IncomeGreen
            TransactionType.EXPENSE  -> "-" to ExpenseRed
            TransactionType.TRANSFER -> "~" to TransferBlue
        }

        Text(
            text       = "$prefix ${CurrencyFormatter.formatRupiah(transaction.amount)}",
            style      = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color      = amountColor,
        )
    }
}

/**
 * Circular icon chip using the category's brand color as background.
 * The icon itself is tinted with the color for contrast.
 */
@Composable
fun CategoryIconChip(
    color              : Color,
    iconResName        : String = "",
    contentDescription : String,
    modifier           : Modifier = Modifier,
) {
    Box(
        modifier          = modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.15f)),
        contentAlignment  = Alignment.Center,
    ) {
        Icon(
            imageVector = IconMapper[iconResName],
            contentDescription = contentDescription,
            tint               = color,
            modifier           = Modifier.size(22.dp),
        )
    }
}
