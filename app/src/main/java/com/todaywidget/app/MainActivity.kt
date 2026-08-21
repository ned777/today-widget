package com.todaywidget.app

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * The screen you land on when either widget (or the app icon) is tapped.
 * Neither widget needs any setup, so there's no device/config list here —
 * just a simple confirmation screen showing today, in the same color
 * language as the widgets.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val today = CalendarUtil.today()

        findViewById<TextView>(R.id.weekdayMonthText).text =
            "${CalendarUtil.weekdayAbbrev(today)} ${CalendarUtil.monthAbbrev(today)}"

        findViewById<TextView>(R.id.bigDayText).text = today.dayOfMonth.toString()

        val fullDateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale.getDefault())
        findViewById<TextView>(R.id.fullDateText).text = today.format(fullDateFormatter)
    }
}
