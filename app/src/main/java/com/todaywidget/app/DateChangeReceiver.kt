package com.todaywidget.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Registered in the manifest for the handful of system broadcasts still
 * allowed to reach a manifest-declared (as opposed to dynamically
 * registered) receiver even after Android 8's crackdown on implicit
 * broadcasts: the calendar day rolling over at midnight, the user manually
 * changing the clock, and the timezone changing (e.g. after landing from a
 * flight). Any of those means "today" may now be a different date than what
 * both widgets last drew, so we repaint them right away — this is what keeps
 * them honest instead of silently freezing on a stale date between the rare
 * 30-minute safety-net ticks in date_widget_info.xml / month_widget_info.xml.
 */
class DateChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        DateWidgetProvider.updateAll(context)
        MonthWidgetProvider.updateAll(context)
    }
}
