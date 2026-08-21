package com.todaywidget.app

import android.app.PendingIntent
import android.content.Context
import android.content.Intent

/**
 * Both widgets do the exact same thing when tapped — open the app — so the
 * PendingIntent-building boilerplate for that lives here once instead of
 * being copy-pasted into DateWidgetProvider and MonthWidgetProvider.
 */
object WidgetLaunch {

    /**
     * @param requestCode  distinguishes PendingIntents across different widget
     *                     instances (their widget id works well for this) so
     *                     tapping one placed widget can never be confused with
     *                     another by the underlying PendingIntent system.
     */
    fun openAppPendingIntent(context: Context, requestCode: Int): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        return PendingIntent.getActivity(
            context, requestCode, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
