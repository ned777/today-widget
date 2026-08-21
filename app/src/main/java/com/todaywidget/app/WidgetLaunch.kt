package com.todaywidget.app

import android.app.PendingIntent
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.provider.CalendarContract

/**
 * Both widgets do the same thing when tapped — hand off to the PHONE'S OWN
 * default calendar app, the same way tapping one of iOS's widgets opens
 * Apple's own app for it rather than opening some in-between screen.
 * Neither widget manages events itself, so there's no reason to route
 * through our own MainActivity here — this PendingIntent-building
 * boilerplate lives here once instead of being copy-pasted into both
 * *WidgetProvider classes.
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
}
