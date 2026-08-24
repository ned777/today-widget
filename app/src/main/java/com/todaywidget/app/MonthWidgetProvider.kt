package com.todaywidget.app

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.widget.RemoteViews

/**
 * The larger month-grid widget: current month only, laid out as a fixed 6x7
 * grid (see widget_month.xml and CalendarUtil.buildMonthGrid()). There's no
 * previous/next-month navigation by design — the whole point, like iOS's
 * equivalent, is that it always just shows "now"; tapping it hands off to
 * the phone's own default calendar app instead of paging in place (see
 * WidgetLaunch.kt).
 *
 * Staying current across midnight is handled the same way as
 * DateWidgetProvider — see DateChangeReceiver.kt.
 */
class MonthWidgetProvider : AppWidgetProvider() {

    companion object {
        // The 7 weekday-header TextView ids, in grid column order.
        private val headerIds = listOf(
        R.id.header_0,
        R.id.header_1,
        R.id.header_2,
        R.id.header_3,
        R.id.header_4,
        R.id.header_5,
        R.id.header_6
        )

        // The 42 day-cell TextView ids, [row][col], matching
        // CalendarUtil.buildMonthGrid()'s 6-row x 7-column shape exactly.
        private val cellIds = listOf(
        listOf(R.id.cell_0_0, R.id.cell_0_1, R.id.cell_0_2, R.id.cell_0_3, R.id.cell_0_4, R.id.cell_0_5, R.id.cell_0_6),
        listOf(R.id.cell_1_0, R.id.cell_1_1, R.id.cell_1_2, R.id.cell_1_3, R.id.cell_1_4, R.id.cell_1_5, R.id.cell_1_6),
        listOf(R.id.cell_2_0, R.id.cell_2_1, R.id.cell_2_2, R.id.cell_2_3, R.id.cell_2_4, R.id.cell_2_5, R.id.cell_2_6),
        listOf(R.id.cell_3_0, R.id.cell_3_1, R.id.cell_3_2, R.id.cell_3_3, R.id.cell_3_4, R.id.cell_3_5, R.id.cell_3_6),
        listOf(R.id.cell_4_0, R.id.cell_4_1, R.id.cell_4_2, R.id.cell_4_3, R.id.cell_4_4, R.id.cell_4_5, R.id.cell_4_6),
        listOf(R.id.cell_5_0, R.id.cell_5_1, R.id.cell_5_2, R.id.cell_5_3, R.id.cell_5_4, R.id.cell_5_5, R.id.cell_5_6)
        )

        /** Repaints every placed instance of this widget with the current month. */
        fun updateAll(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(ComponentName(context, MonthWidgetProvider::class.java))
            ids.forEach { updateOneWidget(context, manager, it) }
        }

        private fun updateOneWidget(context: Context, manager: AppWidgetManager, id: Int) {
            val views = RemoteViews(context.packageName, R.layout.widget_month)
            val today = CalendarUtil.today()

            views.setTextViewText(R.id.monthTitleText, CalendarUtil.monthTitle(today))

            CalendarUtil.weekdayHeaders().forEachIndexed { index, label ->
                views.setTextViewText(headerIds[index], label)
            }

            val grid = CalendarUtil.buildMonthGrid(today)
            grid.forEachIndexed { r, row ->
                row.forEachIndexed { c, cell ->
                    val cellId = cellIds[r][c]
                    views.setTextViewText(cellId, cell.dayOfMonth?.toString() ?: "")
                    // "You are here" is just a color swap now — no circle fill.
                    val color = if (cell.isToday) R.color.retro_cyan else R.color.retro_white
                    views.setTextColor(cellId, context.getColor(color))
                }
            }

            views.setOnClickPendingIntent(R.id.monthWidgetRoot, WidgetLaunch.openCalendarPendingIntent(context, id))

            manager.updateAppWidget(id, views)
        }
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        appWidgetIds.forEach { updateOneWidget(context, appWidgetManager, it) }
    }
}
