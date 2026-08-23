package com.todaywidget.app

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * The screen you land on when the app icon is tapped (the widgets themselves
 * tap straight through to the phone's default calendar app, bypassing this
 * screen entirely). Neither widget needs any setup, so there's no device/
 * config list here — just a simple confirmation screen showing today, in the
 * same color language as the widgets.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Extra safety net alongside DateChangeReceiver's midnight broadcast and the
        // 30-minute timer in date_widget_info.xml/month_widget_info.xml: if either of
        // those ever got delayed or dropped by the OS (background/battery restrictions
        // on a rarely-opened app can do this), opening the app at least self-corrects
        // any placed widgets right here too, since "today" is always computed fresh.
        DateWidgetProvider.updateAll(this)
        MonthWidgetProvider.updateAll(this)

        val today = CalendarUtil.today()

        findViewById<TextView>(R.id.weekdayMonthText).text =
            "${CalendarUtil.weekdayAbbrev(today)} ${CalendarUtil.monthAbbrev(today)}"

        findViewById<TextView>(R.id.bigDayText).text = today.dayOfMonth.toString()

        val fullDateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale.getDefault())
        findViewById<TextView>(R.id.fullDateText).text = today.format(fullDateFormatter)
    }
}
