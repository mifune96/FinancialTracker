package com.aliimran.financialtracker.domain.model

import com.aliimran.financialtracker.util.DateFormatter.toEpochMilli
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

/**
 * Represents the three selectable time windows on the Analytics screen.
 *
 * @param label  Indonesian display label for the tab.
 */
enum class AnalyticsPeriod(val label: String) {
    WEEK("Pekan"),
    MONTH("Bulan"),
    YEAR("Tahun");

    /**
     * Converts this period into an epoch-millisecond [LongRange] anchored
     * to [referenceDate] (defaults to today).
     *
     *  WEEK  → Monday 00:00 … next Monday 00:00 (ISO week)
     *  MONTH → 1st of month … 1st of next month
     *  YEAR  → Jan 1st …  Jan 1st next year
     *
     * The end bound is exclusive (matching SQL `timestamp < endMs`).
     */
    fun toEpochMilliRange(
        referenceDate: LocalDate = LocalDate.now(),
    ): LongRange {
        val (start, end) = when (this) {
            WEEK  -> {
                val monday = referenceDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                monday to monday.plusWeeks(1)
            }
            MONTH -> {
                val firstDay = referenceDate.withDayOfMonth(1)
                firstDay to firstDay.plusMonths(1)
            }
            YEAR  -> {
                val firstDay = referenceDate.withDayOfYear(1)
                firstDay to firstDay.plusYears(1)
            }
        }
        return start.toEpochMilli()..end.toEpochMilli()
    }

    /** Human-readable date-range subtitle shown below the tabs. */
    fun toDisplayRange(referenceDate: LocalDate = LocalDate.now()): String {
        val locale = java.util.Locale("id", "ID")
        val fmt    = java.time.format.DateTimeFormatter.ofPattern("d MMM yyyy", locale)
        return when (this) {
            WEEK  -> {
                val mon = referenceDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                "${mon.format(fmt)} – ${mon.plusDays(6).format(fmt)}"
            }
            MONTH -> {
                val first = referenceDate.withDayOfMonth(1)
                "${first.format(fmt)} – ${first.plusMonths(1).minusDays(1).format(fmt)}"
            }
            YEAR  -> {
                val first = referenceDate.withDayOfYear(1)
                "${first.format(fmt)} – ${first.plusYears(1).minusDays(1).format(fmt)}"
            }
        }
    }
}
