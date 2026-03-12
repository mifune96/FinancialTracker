package com.aliimran.financialtracker.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aliimran.financialtracker.domain.model.TransactionType
import com.aliimran.financialtracker.presentation.theme.ExpenseRed
import com.aliimran.financialtracker.presentation.theme.IncomeGreen
import com.aliimran.financialtracker.presentation.theme.TransferBlue

/**
 * Large, centred amount display card shown at the top of the
 * Add Transaction screen.
 *
 * Animates digit changes with a vertical slide so the entry
 * feels responsive without a system keyboard.
 *
 * @param amountFormatted  Thousand-separated string, e.g. "1.500.000".
 * @param transactionType  Drives the accent color of the amount text.
 * @param hasImage         Whether a receipt image has been attached.
 * @param modifier         External layout modifier.
 */
@Composable
fun AmountDisplay(
    amountFormatted : String,
    transactionType : TransactionType,
    hasImage        : Boolean,
    modifier        : Modifier = Modifier,
) {
    val accentColor: Color = when (transactionType) {
        TransactionType.EXPENSE  -> ExpenseRed
        TransactionType.INCOME   -> IncomeGreen
        TransactionType.TRANSFER -> TransferBlue
    }

    // Dynamically reduce font size for very long amounts
    val fontSize = when {
        amountFormatted.length > 12 -> 28.sp
        amountFormatted.length > 9  -> 36.sp
        amountFormatted.length > 6  -> 42.sp
        else                        -> 48.sp
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 24.dp, vertical = 20.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            // ── Currency prefix ───────────────────────────────
            Text(
                text  = "Rp",
                style = MaterialTheme.typography.labelLarge,
                color = accentColor.copy(alpha = 0.7f),
            )

            Spacer(modifier = Modifier.height(4.dp))

            // ── Animated amount digits ────────────────────────
            AnimatedContent(
                targetState   = amountFormatted,
                transitionSpec = {
                    slideInVertically { it } togetherWith slideOutVertically { -it }
                },
                label = "amount_animation",
            ) { displayedAmount ->
                Text(
                    text       = displayedAmount,
                    fontSize   = fontSize,
                    fontWeight = FontWeight.Bold,
                    color      = accentColor,
                    textAlign  = TextAlign.Center,
                )
            }

            // ── Image indicator ───────────────────────────────
            if (hasImage) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier              = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(accentColor.copy(alpha = 0.1f))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                ) {
                    Icon(
                        imageVector        = Icons.Outlined.Image,
                        contentDescription = null,
                        tint               = accentColor,
                        modifier           = Modifier.size(14.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text  = "Foto terlampir",
                        style = MaterialTheme.typography.labelSmall,
                        color = accentColor,
                    )
                }
            }
        }
    }
}
