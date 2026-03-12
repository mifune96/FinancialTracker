package com.aliimran.financialtracker.util

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

/**
 * Centralised date/time formatting utilities.
 * All conversions use the device's default ZoneId unless specified.
 */
object DateFormatter {

    private val LOCALE_ID = Locale("id", "ID")   // Indonesian locale

    /** "Senin, 12 Maret 2024" — full date header for grouped transaction rows. */
    private val FULL_DATE_FORMATTER =
        DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", LOCALE_ID)

    /** "12 Mar" — compact date for chart axis labels. */
    private val SHORT_DATE_FORMATTER =
        DateTimeFormatter.ofPattern("d MMM", LOCALE_ID)

    /** "Maret 2024" — month/year picker display label. */
    private val MONTH_YEAR_FORMATTER =
        DateTimeFormatter.ofPattern("MMMM yyyy", LOCALE_ID)

    // ── Converters ────────────────────────────────────────────

    /** Converts a Unix epoch millisecond timestamp to [LocalDate]. */
    fun Long.toLocalDate(zone: ZoneId = ZoneId.systemDefault()): LocalDate =
        Instant.ofEpochMilli(this).atZone(zone).toLocalDate()

    /** Converts a [LocalDate] to a Unix epoch millisecond timestamp (start of day). */
    fun LocalDate.toEpochMilli(zone: ZoneId = ZoneId.systemDefault()): Long =
        atStartOfDay(zone).toInstant().toEpochMilli()

    // ── Formatters ────────────────────────────────────────────

    /** Formats a [LocalDate] as "Senin, 12 Maret 2024". */
    fun LocalDate.toFullDateString(): String = FULL_DATE_FORMATTER.format(this)

    /** Formats a [LocalDate] as "12 Mar". */
    fun LocalDate.toShortDateString(): String = SHORT_DATE_FORMATTER.format(this)

    /** Formats a [LocalDate] as "Maret 2024". */
    fun LocalDate.toMonthYearString(): String = MONTH_YEAR_FORMATTER.format(this)

    /**
     * Returns the full Indonesian month name for a 1-based month integer.
     * e.g. 3 → "Maret"
     */
    fun monthDisplayName(month: Int, year: Int): String =
        LocalDate.of(year, month, 1)
            .month
            .getDisplayName(TextStyle.FULL_STANDALONE, LOCALE_ID)
            .replaceFirstChar { it.uppercase() }

    /** Returns a list of all 12 month display names in Indonesian. */
    fun allMonthNames(): List<String> =
        (1..12).map { monthDisplayName(it, 2024) }
}
