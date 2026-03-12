package com.aliimran.financialtracker.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aliimran.financialtracker.domain.model.CategoryAnalytics
import com.aliimran.financialtracker.util.CurrencyFormatter
import com.aliimran.financialtracker.util.Extensions.toComposeColor

/**
 * A single row in the category breakdown list below the Donut chart.
 *
 * Layout:
 * ┌──────────────────────────────────────────────────────────────┐
 * │  ● Makanan & Minum          35%                Rp 525.000   │
 * │    ████████████░░░░░░░░░░░░                                  │
 * │    12 transaksi                                              │
 * └──────────────────────────────────────────────────────────────┘
 *
 * @param analytics    The category data to display.
 * @param isSelected   Whether this row matches the selected chart segment.
 * @param onClick      Called when the row is tapped.
 * @param rank         1-based rank label (position in sorted list).
 * @param modifier     External modifier.
 */
@Composable
fun CategorySpendingRow(
    analytics  : CategoryAnalytics,
    isSelected : Boolean,
    onClick    : () -> Unit,
    rank       : Int,
    modifier   : Modifier = Modifier,
) {
    val chipColor = analytics.category.color.toComposeColor()

    // Animate the progress bar width on first composition and on selection
    var targetProgress by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(analytics) { targetProgress = analytics.percentage / 100f }

    val animatedProgress by animateFloatAsState(
        targetValue   = targetProgress,
        animationSpec = tween(durationMillis = 800),
        label         = "progress_${analytics.category.id}",
    )

    val rowBackground = if (isSelected)
        chipColor.copy(alpha = 0.08f)
    else
        Color.Transparent

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(rowBackground)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
    ) {
        // ── Top row: color dot + name + percentage + amount ───
        Row(
            modifier              = Modifier.fillMaxWidth(),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            // Colored dot
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(chipColor),
            )

            // Category name (expands)
            Text(
                text       = analytics.category.name,
                style      = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color      = if (isSelected) chipColor
                             else MaterialTheme.colorScheme.onSurface,
                modifier   = Modifier.weight(1f),
                maxLines   = 1,
                overflow   = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
            )

            // Percentage badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(chipColor.copy(alpha = if (isSelected) 0.20f else 0.10f))
                    .padding(horizontal = 8.dp, vertical = 2.dp),
            ) {
                Text(
                    text       = "${analytics.percentage.toInt()}%",
                    fontSize   = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color      = chipColor,
                )
            }

            Spacer(Modifier.width(4.dp))

            // Amount
            Text(
                text       = CurrencyFormatter.formatRupiah(analytics.totalAmount),
                style      = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color      = MaterialTheme.colorScheme.onSurface,
            )
        }

        Spacer(Modifier.height(6.dp))

        // ── Animated progress bar ─────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .clip(RoundedCornerShape(50))
                .background(chipColor.copy(alpha = 0.12f)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .height(5.dp)
                    .clip(RoundedCornerShape(50))
                    .background(chipColor),
            )
        }

        Spacer(Modifier.height(4.dp))

        // ── Transaction count ─────────────────────────────────
        Text(
            text  = "${analytics.transactionCount} transaksi",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
