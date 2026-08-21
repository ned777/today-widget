package com.todaywidget.app

import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.Locale

/**
 * All the "what does today/this month look like" math lives here, shared by
 * both widgets (and the app screen) so there's exactly one place that knows
 * how to turn "right now" into display text — no duplicated date logic to
 * keep in sync between DateWidgetProvider and MonthWidgetProvider.
 */
object CalendarUtil {

    fun today(): LocalDate = LocalDate.now()

    // e.g. "FRI" — the small widget's weekday label.
    fun weekdayAbbrev(date: LocalDate): String =
        date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()).uppercase(Locale.getDefault())

    // e.g. "AUG" — the small widget's month label.
    fun monthAbbrev(date: LocalDate): String =
        date.month.getDisplayName(TextStyle.SHORT, Locale.getDefault()).uppercase(Locale.getDefault())

    // e.g. "August 2026" — the month grid widget's title line.
    fun monthTitle(date: LocalDate): String =
        "${date.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${date.year}"

    /** One cell of the month grid: either blank (outside the current month) or a real day. */
    data class MonthCell(val dayOfMonth: Int?, val isToday: Boolean)

    /**
     * Builds today's month as a fixed 6-row x 7-column grid, always — a
     * calendar month is always exactly 6 weeks-wide at most, so the shape
     * never varies, only which cells are blank. Cells before the 1st and
     * after the last day of the month are blank (no leading/trailing days
     * from adjacent months, matching "just stay current" — this widget
     * doesn't try to imply you could tap into another month).
     */
    fun buildMonthGrid(date: LocalDate): List<List<MonthCell>> {
        val yearMonth = YearMonth.from(date)
        val firstOfMonth = yearMonth.atDay(1)
        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
        val leadingBlanks = ((firstOfMonth.dayOfWeek.value - firstDayOfWeek.value) + 7) % 7
        val daysInMonth = yearMonth.lengthOfMonth()

        val cells = mutableListOf<MonthCell>()
        repeat(leadingBlanks) { cells.add(MonthCell(null, false)) }
        for (day in 1..daysInMonth) {
            cells.add(MonthCell(day, day == date.dayOfMonth))
        }
        while (cells.size < 42) cells.add(MonthCell(null, false))

        return cells.chunked(7)
    }

    /**
     * Weekday header labels ("S M T W T F S" or similar), in the SAME
     * first-day-of-week order buildMonthGrid() used above — both derive
     * from the same Locale.getDefault() first-day-of-week setting, so the
     * headers and the grid underneath them always line up.
     */
    fun weekdayHeaders(): List<String> {
        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
        return (0..6).map { offset ->
            firstDayOfWeek.plus(offset.toLong())
                .getDisplayName(TextStyle.NARROW, Locale.getDefault())
                .uppercase(Locale.getDefault())
        }
    }
}
