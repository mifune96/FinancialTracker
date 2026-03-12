package com.aliimran.financialtracker.util

import androidx.compose.ui.graphics.Color

object Extensions {
    /** Converts an ARGB Int to a Compose [Color]. */
    fun Int.toComposeColor(): Color = Color(this)

    /** Clamps this Double to a minimum of 0.0 — avoids negative display values. */
    fun Double.coerceNonNegative(): Double = coerceAtLeast(0.0)

    /** Returns true if this string represents a valid positive number. */
    fun String.isValidAmount(): Boolean =
        toDoubleOrNull()?.let { it > 0 } ?: false
}

