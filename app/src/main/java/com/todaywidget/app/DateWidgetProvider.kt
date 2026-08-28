package com.todaywidget.app

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.util.TypedValue
import android.widget.RemoteViews

/**
 * The small "21 / Fri Aug" widget — an iOS-calendar-icon-style glance at
 * today's date, big day number on top, weekday + month below it. There's
 * nothing to configure and nothing to fetch; every instance always just
 * shows today, computed fresh each time it redraws. Tapping it opens the
 * phone's own default calendar app (see WidgetLaunch.kt).
 *
 * Staying current (instead of freezing on whatever date it happened to be
 * drawn on) is handled by DateChangeReceiver, which calls updateAll() below
 * whenever the system tells us the date/time/timezone changed — see that
 * file for why a plain AppWidgetProvider can't just be left to sleep between
 * home-screen taps the way SysMonWidgetProvider could.
 */
class DateWidgetProvider : AppWidgetProvider() {

    companion object {
        // Must track widget_date.xml's headerRow styling exactly (bold,
        // 30sp, 0.04 letter-spacing, 4dp spacer) — this is what lets
        // measureHeaderWidthPx() below predict how wide that row will
        // actually render, without RemoteViews giving us any way to ask the
        // real inflated view for its width directly.
        private const val HEADER_TEXT_SIZE_SP = 30f
        private const val HEADER_LETTER_SPACING = 0.04f
        private const val HEADER_SPACER_DP = 4f

        /** Repaints every placed instance of this widget with today's date. */
        fun updateAll(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(ComponentName(context, DateWidgetProvider::class.java))
            ids.forEach { updateOneWidget(context, manager, it) }
        }

        private fun updateOneWidget(context: Context, manager: AppWidgetManager, id: Int) {
            val views = RemoteViews(context.packageName, R.layout.widget_date)
            val today = CalendarUtil.today()
            val weekday = CalendarUtil.weekdayAbbrev(today)
            val month = CalendarUtil.monthAbbrev(today)
            val dayString = "%02d".format(today.dayOfMonth)

            views.setTextViewText(R.id.weekdayText, weekday)
            views.setTextViewText(R.id.monthText, month)
            views.setTextViewText(R.id.dayText, dayString)

            // The day number's font size is picked fresh every update so its
            // rendered width always matches headerRow's rendered width — every
            // different weekday/month combo needs a different size to line up.
            // dayString is always zero-padded to 2 digits (see above) so this
            // never has to size a lone digit up to match a 2-char-wide header.
            val metrics = context.resources.displayMetrics
            val headerWidthPx = measureHeaderWidthPx(metrics, weekday, month)
            val daySizeSp = computeDaySizeSp(metrics, headerWidthPx, dayString)
            views.setTextViewTextSize(R.id.dayText, TypedValue.COMPLEX_UNIT_SP, daySizeSp)

            views.setOnClickPendingIntent(R.id.dateWidgetRoot, WidgetLaunch.openCalendarPendingIntent(context, id))

            manager.updateAppWidget(id, views)
        }

        // Width of "<weekday>  <month>" as headerRow will actually render it.
        private fun measureHeaderWidthPx(metrics: android.util.DisplayMetrics, weekday: String, month: String): Float {
            val paint = Paint().apply {
                typeface = Typeface.DEFAULT_BOLD
                textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, HEADER_TEXT_SIZE_SP, metrics)
                letterSpacing = HEADER_LETTER_SPACING
            }
            val spacerPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, HEADER_SPACER_DP, metrics)
            return paint.measureText(weekday) + spacerPx + paint.measureText(month)
        }

        // Text width scales linearly with font size for a fixed string, so
        // measuring once at an arbitrary probe size and scaling by the ratio
        // to the target width gives the exact size needed — no iteration.
        private fun computeDaySizeSp(metrics: android.util.DisplayMetrics, targetWidthPx: Float, dayString: String): Float {
            val probeSizeSp = 40f
            val paint = Paint().apply {
                typeface = Typeface.DEFAULT_BOLD
                textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, probeSizeSp, metrics)
            }
            val measuredPx = paint.measureText(dayString)
            if (measuredPx <= 0f) return probeSizeSp
            return probeSizeSp * (targetWidthPx / measuredPx)
        }
    }

    // Called by Android when this widget needs updating "normally" — first
    // placed, or (see date_widget_info.xml) on its 30-minute safety-net timer.
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        appWidgetIds.forEach { updateOneWidget(context, appWidgetManager, it) }
    }
}
