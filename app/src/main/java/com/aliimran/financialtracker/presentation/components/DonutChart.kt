package com.aliimran.financialtracker.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aliimran.financialtracker.domain.model.CategoryAnalytics
import com.aliimran.financialtracker.util.CurrencyFormatter
import com.aliimran.financialtracker.util.Extensions.toComposeColor
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * Donut chart drawn entirely with Compose [Canvas] — no third-party libraries.
 *
 * Features:
 *  • Animated reveal: all segments grow from 0° to their target sweep over
 *    [animDurationMs] milliseconds using a single [Animatable] progress value.
 *  • Interactive: tap a segment to select it. The selected segment is drawn
 *    with a larger outer radius (+[selectionOffset]).
 *  • Center content: shows the selected category's name + amount, or the
 *    grand total when nothing is selected.
 *  • Empty state: draws a single grey placeholder ring.
 *  • Gap between segments: [gapDegrees] prevents colors from bleeding together.
 *
 * @param segments            Ordered list of category analytics (must have sweepAngle set).
 * @param grandTotal          Grand total to show in the center when no segment is selected.
 * @param selectedIndex       Index of the currently selected segment (null = none).
 * @param onSegmentTapped     Callback with the tapped segment index.
 * @param chartSize           Total composable size (default 260dp is good for most phones).
 * @param strokeWidth         Donut ring thickness.
 * @param gapDegrees          Angular gap between adjacent segments.
 * @param selectionOffset     Extra radius added to the selected segment arc.
 * @param animDurationMs      Reveal animation duration in milliseconds.
 */
@Composable
fun DonutChart(
    segments        : List<CategoryAnalytics>,
    grandTotal      : Double,
    selectedIndex   : Int?                          = null,
    onSegmentTapped : (index: Int) -> Unit          = {},
    chartSize       : Dp                            = 260.dp,
    strokeWidth     : Dp                            = 44.dp,
    gapDegrees      : Float                         = 3f,
    selectionOffset : Dp                            = 8.dp,
    animDurationMs  : Int                           = 900,
    modifier        : Modifier                      = Modifier,
) {
    // Single Animatable drives the entire reveal animation (0f → 1f)
    val progress = remember(segments) { Animatable(0f) }

    LaunchedEffect(segments) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue   = 1f,
            animationSpec = tween(durationMillis = animDurationMs),
        )
    }

    Box(
        modifier         = modifier.size(chartSize),
        contentAlignment = Alignment.Center,
    ) {
        // ── Canvas for the arcs ───────────────────────────────
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(segments, selectedIndex) {
                    detectTapGestures { tapOffset ->
                        val tappedIndex = detectSegmentTap(
                            tapOffset   = tapOffset,
                            canvasSize  = size,
                            strokePx    = strokeWidth.toPx(),
                            segments    = segments,
                            progressVal = progress.value,
                            gapDegrees  = gapDegrees,
                        )
                        if (tappedIndex != null) onSegmentTapped(tappedIndex)
                    }
                },
        ) {
            val p = progress.value

            if (segments.isEmpty() || grandTotal == 0.0) {
                drawEmptyRing(strokeWidth.toPx())
                return@Canvas
            }

            var startAngle = -90f  // 12 o'clock

            segments.forEachIndexed { index, segment ->
                val isSelected  = index == selectedIndex
                val strokePx    = strokeWidth.toPx()
                val offsetPx    = if (isSelected) selectionOffset.toPx() else 0f

                // Animated sweep shrunk by gap (minimum 0f to avoid negative arc)
                val rawSweep    = segment.sweepAngle * p
                val effectiveSweep = (rawSweep - gapDegrees * p).coerceAtLeast(0f)

                if (effectiveSweep > 0f) {
                    drawDonutArc(
                        color       = segment.category.color.toComposeColor(),
                        startAngle  = startAngle + (gapDegrees * p / 2f),
                        sweepAngle  = effectiveSweep,
                        strokeWidth = strokePx,
                        offsetPx    = offsetPx,
                    )
                }
                startAngle += rawSweep
            }
        }

        // ── Center label ──────────────────────────────────────
        DonutCenterLabel(
            selectedSegment = selectedIndex?.let { segments.getOrNull(it) },
            grandTotal      = grandTotal,
        )
    }
}

// ─────────────────────────────────────────────────────────────
// DrawScope helpers
// ─────────────────────────────────────────────────────────────

/**
 * Draws a single donut arc.
 * [offsetPx] expands the arc outward (for the selection highlight effect).
 */
private fun DrawScope.drawDonutArc(
    color       : Color,
    startAngle  : Float,
    sweepAngle  : Float,
    strokeWidth : Float,
    offsetPx    : Float = 0f,
) {
    val inset = strokeWidth / 2f + offsetPx
    val topLeft = Offset(inset - offsetPx, inset - offsetPx)
    val arcSize = Size(
        width  = size.width  - strokeWidth + offsetPx * 2,
        height = size.height - strokeWidth + offsetPx * 2,
    )
    drawArc(
        color      = color,
        startAngle = startAngle,
        sweepAngle = sweepAngle,
        useCenter  = false,
        topLeft    = topLeft.copy(
            x = (size.width  - arcSize.width)  / 2f,
            y = (size.height - arcSize.height) / 2f,
        ),
        size       = arcSize,
        style      = Stroke(width = strokeWidth + offsetPx * 2, cap = StrokeCap.Butt),
    )
}

/** Draws a single grey circle for the empty / loading state. */
private fun DrawScope.drawEmptyRing(strokeWidth: Float) {
    val inset   = strokeWidth / 2f
    val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
    drawArc(
        color      = Color.LightGray.copy(alpha = 0.3f),
        startAngle = 0f,
        sweepAngle = 360f,
        useCenter  = false,
        topLeft    = Offset(inset, inset),
        size       = arcSize,
        style      = Stroke(width = strokeWidth),
    )
}

// ─────────────────────────────────────────────────────────────
// Tap Detection
// ─────────────────────────────────────────────────────────────

/**
 * Maps a tap [tapOffset] to a segment index by computing the polar
 * angle of the tap relative to the chart center.
 *
 * Returns null if the tap misses all segments (e.g. inside the hole
 * or outside the ring).
 */
private fun detectSegmentTap(
    tapOffset   : Offset,
    canvasSize  : androidx.compose.ui.unit.IntSize,
    strokePx    : Float,
    segments    : List<CategoryAnalytics>,
    progressVal : Float,
    gapDegrees  : Float,
): Int? {
    val center    = Offset(canvasSize.width / 2f, canvasSize.height / 2f)
    val radius    = (canvasSize.width / 2f)
    val innerR    = radius - strokePx
    val outerR    = radius

    val dx = tapOffset.x - center.x
    val dy = tapOffset.y - center.y
    val dist = Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()

    // Reject taps outside the ring
    if (dist < innerR || dist > outerR) return null

    // Compute tap angle in [0, 360) starting from 12 o'clock
    var tapAngle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat() + 90f
    if (tapAngle < 0f) tapAngle += 360f

    var startAngle = 0f   // Cumulative from 0 (top)
    segments.forEachIndexed { index, segment ->
        val sweep = segment.sweepAngle * progressVal
        if (tapAngle >= startAngle && tapAngle < startAngle + sweep) {
            return index
        }
        startAngle += sweep
    }
    return null
}

// ─────────────────────────────────────────────────────────────
// Center Label
// ─────────────────────────────────────────────────────────────

@Composable
private fun DonutCenterLabel(
    selectedSegment : CategoryAnalytics?,
    grandTotal      : Double,
    modifier        : Modifier = Modifier,
) {
    Column(
        modifier            = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (selectedSegment != null) {
            // Show selected category details
            Text(
                text       = selectedSegment.category.name,
                style      = MaterialTheme.typography.labelMedium,
                color      = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign  = TextAlign.Center,
                maxLines   = 2,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text       = CurrencyFormatter.formatRupiah(selectedSegment.totalAmount),
                fontWeight = FontWeight.Bold,
                fontSize   = 16.sp,
                color      = selectedSegment.category.color.toComposeColor(),
                textAlign  = TextAlign.Center,
            )
            Text(
                text  = "${selectedSegment.percentage.toInt()}%",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            // Show grand total
            Text(
                text  = "Total",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text       = CurrencyFormatter.formatRupiah(grandTotal),
                fontWeight = FontWeight.Bold,
                fontSize   = 16.sp,
                color      = MaterialTheme.colorScheme.onSurface,
                textAlign  = TextAlign.Center,
            )
        }
    }
}
