package com.todaywidget.app

import android.app.PendingIntent
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.provider.AlarmClock
import android.provider.CalendarContract

/**
 * Every widget does the same kind of thing when tapped — hand off to
 * whatever PHONE'S OWN app actually owns that data (calendar or clock),
 * the same way tapping one of iOS's widgets opens Apple's own app for it
 * rather than opening some in-between screen. None of our widgets manage
 * events or alarms themselves, so there's no reason to route through our
 * own MainActivity here — this PendingIntent-building boilerplate lives
 * here once instead of being copy-pasted into every *WidgetProvider.
 */
object WidgetLaunch {

    /**
     * Building a CalendarContract "view this moment" URI and firing a plain
     * ACTION_VIEW at it is the standard, permission-free way to hand off to
     * whatever calendar app is installed (Google Calendar, Samsung Calendar,
     * etc.) — no need to know its package name, and no READ_CALENDAR
     * permission required since we're not reading calendar data ourselves,
     * just asking another app to open.
     *
     * @param requestCode  distinguishes PendingIntents across different widget
     *                     instances (their widget id works well for this) so
     *                     tapping one placed widget can never be confused with
     *                     another by the underlying PendingIntent system.
     */
    fun openCalendarPendingIntent(context: Context, requestCode: Int): PendingIntent {
        val uri = ContentUris.appendId(
            CalendarContract.CONTENT_URI.buildUpon().appendPath("time"),
            System.currentTimeMillis()
        ).build()
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        return PendingIntent.getActivity(
            context, requestCode, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    /**
     * AlarmClock.ACTION_SHOW_ALARMS is the standard, permission-free way to
     * hand off to whatever clock app is installed and showing its alarms
     * screen — the closest equivalent to tapping into the calendar app
     * above, for a widget that only shows the time.
     */
    fun openClockPendingIntent(context: Context, requestCode: Int): PendingIntent {
        val intent = Intent(AlarmClock.ACTION_SHOW_ALARMS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        return PendingIntent.getActivity(
            context, requestCode, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
