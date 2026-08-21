package com.todaywidget.app

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.widget.RemoteViews

/**
 * The small "Fri Aug / 21" widget — an iOS-calendar-icon-style glance at
 * today's date. There's nothing to configure and nothing to fetch; every
 * instance always just shows today, computed fresh each time it redraws.
 * Tapping it opens the phone's own default calendar app (see WidgetLaunch.kt).
 *
 * Staying current (instead of freezing on whatever date it happened to be
 * drawn on) is handled by DateChangeReceiver, which calls updateAll() below
 * whenever the system tells us the date/time/timezone changed — see that
 * file for why a plain AppWidgetProvider can't just be left to sleep between
 * home-screen taps the way SysMonWidgetProvider could.
 */
class DateWidgetProvider : AppWidgetProvider() {

    companion object {
        /** Repaints every placed instance of this widget with today's date. */
        fun updateAll(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(ComponentName(context, DateWidgetProvider::class.java))
            ids.forEach { updateOneWidget(context, manager, it) }
        }

        private fun updateOneWidget(context: Context, manager: AppWidgetManager, id: Int) {
            val views = RemoteViews(context.packageName, R.layout.widget_date)
            val today = CalendarUtil.today()

            views.setTextViewText(R.id.weekdayText, CalendarUtil.weekdayAbbrev(today))
            views.setTextViewText(R.id.monthText, CalendarUtil.monthAbbrev(today))
            views.setTextViewText(R.id.dayText, today.dayOfMonth.toString())
            views.setOnClickPendingIntent(R.id.dateWidgetRoot, WidgetLaunch.openCalendarPendingIntent(context, id))

            manager.updateAppWidget(id, views)
        }
    }

    // Called by Android when this widget needs updating "normally" — first
    // placed, or (see date_widget_info.xml) on its 30-minute safety-net timer.
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        appWidgetIds.forEach { updateOneWidget(context, appWidgetManager, it) }
    }
}
