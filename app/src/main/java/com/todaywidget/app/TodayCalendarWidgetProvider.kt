package com.todaywidget.app

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.widget.RemoteViews
import java.time.format.TextStyle
import java.util.Locale

/**
 * The 4x2 "Today + Calendar" widget — the Today Clock and the Today Calendar
 * merged side by side: today's big day number with the full weekday
 * ("TUESDAY") under it on the left, the current month grid on the right.
 *
 * The weekday's text size is computed each update so it renders exactly as
 * wide as the day number above it: a short day ("FRIDAY") gets bigger text, a
 * long one ("WEDNESDAY") smaller, and both always line up flush with the
 * number. Same idea as DateWidgetProvider, just matched the other way round.
 *
 * Kept current across midnight by DateChangeReceiver, like the other two
 * widgets; tapping it opens the phone's calendar app (WidgetLaunch.kt).
 */
class TodayCalendarWidgetProvider : AppWidgetProvider() {

    companion object {
        // Big day number; the weekday under it is sized to match its width.
        private const val DAY_SIZE_SP = 80f
        private const val WEEKDAY_LETTER_SPACING = 0.04f

        /** Repaints every placed instance of this widget with today's date. */
        fun updateAll(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(ComponentName(context, TodayCalendarWidgetProvider::class.java))
            ids.forEach { updateOneWidget(context, manager, it) }
        }

        private fun updateOneWidget(context: Context, manager: AppWidgetManager, id: Int) {
            val views = RemoteViews(context.packageName, R.layout.widget_today_calendar)
            WidgetCorners.apply(views, R.id.comboWidgetRoot, manager, id)
            val today = CalendarUtil.today()

            val dayString = "%02d".format(today.dayOfMonth)
            val weekday = today.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault()).uppercase(Locale.getDefault())
            views.setTextViewText(R.id.comboDayText, dayString)
            views.setTextViewText(R.id.comboWeekdayText, weekday)

            // Sizes are plain sp (like the rest of the layout), NOT the Today Clock's
            // stable-density px: on a phone running a lowered screen resolution the
            // stable density is far higher than the real one, which blew the number
            // up so only "1" fit. The weekday is sized as a ratio of the number,
            // which doesn't depend on density at all.
            views.setTextViewTextSize(R.id.comboDayText, TypedValue.COMPLEX_UNIT_SP, DAY_SIZE_SP)
            val probe = 100f
            val dayWidth = Paint().apply {
                typeface = Typeface.DEFAULT_BOLD
                textSize = probe
            }.measureText(dayString)
            val weekdayWidth = Paint().apply {
                typeface = Typeface.DEFAULT_BOLD
                textSize = probe
                letterSpacing = WEEKDAY_LETTER_SPACING
            }.measureText(weekday)
            if (weekdayWidth > 0f) {
                views.setTextViewTextSize(R.id.comboWeekdayText, TypedValue.COMPLEX_UNIT_SP, DAY_SIZE_SP * dayWidth / weekdayWidth)
            }

            val week = today.get(java.time.temporal.IsoFields.WEEK_OF_WEEK_BASED_YEAR)
            val daysLeft = today.lengthOfYear() - today.dayOfYear
            views.setTextViewText(R.id.comboWeekText, "WEEK $week · $daysLeft LEFT")

            MonthWidgetProvider.bindMonth(context, views, today, highlightStyle = true)

            views.setOnClickPendingIntent(R.id.comboWidgetRoot, WidgetLaunch.openCalendarPendingIntent(context, id))
            manager.updateAppWidget(id, views)
        }
    }

    // First placed, or the 30-minute safety-net timer (see today_calendar_widget_info.xml).
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        appWidgetIds.forEach { updateOneWidget(context, appWidgetManager, it) }
    }

    // The launcher re-scales the widget when the home-screen grid changes; redraw
    // so the border radius follows the new scale.
    override fun onAppWidgetOptionsChanged(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, newOptions: Bundle) {
        updateOneWidget(context, appWidgetManager, appWidgetId)
    }
}
