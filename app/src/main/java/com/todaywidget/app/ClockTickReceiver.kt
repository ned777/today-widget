package com.todaywidget.app

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Keeps ClockWidgetProvider visibly ticking once a minute. Android widgets
 * have no built-in update hook fine-grained enough for a clock —
 * updatePeriodMillis is capped at a 30-minute minimum, and the system's own
 * once-a-minute ACTION_TIME_TICK broadcast is deliberately NOT allowed to
 * reach a manifest-declared receiver (that restriction is what stops every
 * app on the phone from waking up every single minute). The standard
 * workaround, used here, is a self-rescheduling AlarmManager chain: each
 * time this fires, it repaints the clock and immediately arms the next
 * firing for the next minute boundary.
 */
class ClockTickReceiver : BroadcastReceiver() {

    companion object {
        private const val REQUEST_CODE = 4200

        private fun pendingIntent(context: Context): PendingIntent {
            val intent = Intent(context, ClockTickReceiver::class.java)
            return PendingIntent.getBroadcast(
                context, REQUEST_CODE, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        /**
         * Arms (or re-arms) the alarm for the next minute boundary. Safe to
         * call repeatedly — pendingIntent() above always resolves to the
         * same PendingIntent, so a new call just replaces the pending one
         * instead of stacking up extras. setAndAllowWhileIdle (rather than
         * the exact variants) deliberately avoids needing the special
         * "Alarms & reminders" permission Android 12+ requires for exact
         * alarms — a few seconds of slack is invisible on a minutes-only
         * digital clock face.
         */
        fun scheduleNextTick(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val now = System.currentTimeMillis()
            val nextMinuteBoundary = now - (now % 60_000L) + 60_000L
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC, nextMinuteBoundary, pendingIntent(context))
        }

        /** Stops the tick chain — called once the last clock widget is removed. */
        fun cancelTicks(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent(context))
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        ClockWidgetProvider.updateAll(context)
        scheduleNextTick(context)
    }
}
