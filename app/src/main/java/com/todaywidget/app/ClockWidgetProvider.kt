package com.todaywidget.app

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.widget.RemoteViews
import java.time.LocalTime

/**
 * The round retro clock widget: just the current hour and minute, stacked,
 * no seconds. Ticking once a minute is driven by ClockTickReceiver's
 * self-rescheduling alarm chain, armed here whenever a widget instance
 * exists (onEnabled/onUpdate) and stopped once none remain (onDisabled), so
 * the phone isn't woken up every minute for a clock nobody has placed.
 * Tapping the widget opens the phone's own default clock app (see
 * WidgetLaunch.kt).
 */
class ClockWidgetProvider : AppWidgetProvider() {

    companion object {
        /** Repaints every placed instance of this widget with the current time. */
        fun updateAll(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(ComponentName(context, ClockWidgetProvider::class.java))
            ids.forEach { updateOneWidget(context, manager, it) }
        }

        private fun updateOneWidget(context: Context, manager: AppWidgetManager, id: Int) {
            val views = RemoteViews(context.packageName, R.layout.widget_clock)
            val now = LocalTime.now()

            views.setTextViewText(R.id.hourText, "%02d".format(now.hour))
            views.setTextViewText(R.id.minuteText, "%02d".format(now.minute))
            views.setOnClickPendingIntent(R.id.clockWidgetRoot, WidgetLaunch.openClockPendingIntent(context, id))

            manager.updateAppWidget(id, views)
        }
    }

    // Called by Android when this widget needs updating "normally" — first
    // placed, or (see clock_widget_info.xml) on its 30-minute safety-net
    // timer. Also re-arms the tick chain in case it was ever lost.
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        appWidgetIds.forEach { updateOneWidget(context, appWidgetManager, it) }
        ClockTickReceiver.scheduleNextTick(context)
    }

    // Called once when the FIRST instance of this widget is placed anywhere.
    override fun onEnabled(context: Context) {
        ClockTickReceiver.scheduleNextTick(context)
    }

    // Called once when the LAST instance of this widget is removed — stop
    // waking the phone up every minute for a clock nobody has anymore.
    override fun onDisabled(context: Context) {
        ClockTickReceiver.cancelTicks(context)
    }
}
