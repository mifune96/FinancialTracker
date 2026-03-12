package com.aliimran.financialtracker.util

import java.text.NumberFormat
import java.util.Locale

/**
 * Centralised currency formatting utilities.
 *
 * The default locale is Indonesian Rupiah (IDR), which matches the app's
 * primary target market.  Override [locale] and [currencyCode] when
 * internationalising.
 */
object CurrencyFormatter {

    private val IDR_LOCALE = Locale("id", "ID")

    /**
     * Formats a Double as a localised Rupiah string.
     * e.g. 150000.0 → "Rp 150.000"
     */
    fun formatRupiah(amount: Double): String {
        val formatter = NumberFormat.getCurrencyInstance(IDR_LOCALE)
        // Remove decimal digits for whole-number Rupiah display.
        formatter.maximumFractionDigits = 0
        return formatter.format(amount)
    }

    /**
     * Formats a Double with thousand separators but no currency symbol.
     * e.g. 1500000.0 → "1.500.000"
     */
    fun formatAmount(amount: Double): String {
        val formatter = NumberFormat.getNumberInstance(IDR_LOCALE)
        formatter.maximumFractionDigits = 0
        return formatter.format(amount)
    }

    /**
     * Parses a localised amount string back to Double.
     * Returns null if the string cannot be parsed.
     */
    fun parseAmount(value: String): Double? =
        runCatching {
            val cleaned = value.replace("[^\\d]".toRegex(), "")
            cleaned.toDoubleOrNull()
        }.getOrNull()
}
