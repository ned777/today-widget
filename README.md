# Today Widget

Three iOS-style home-screen widgets for Android, plus a minimal app screen.

## Widgets

- **Date widget** — a small square glance at today: weekday + month
  (e.g. "Fri Aug") with a big day-of-month number below (e.g. "21").
- **Month widget** — a full current-month calendar grid, today highlighted.
  There's no previous/next-month navigation — like iOS's equivalent, it
  always just shows "now."
- **Clock widget** — a round retro digital clock: hour stacked above minute,
  both double-digit, no seconds. Ticks once a minute via a self-rescheduling
  alarm — see `ClockTickReceiver.kt`.

All three widgets:
- Refresh themselves automatically (the date/month widgets at midnight and
  on manual clock/timezone changes — see `DateChangeReceiver.kt`; the clock
  widget once a minute — see `ClockTickReceiver.kt`), so none of them freeze
  on stale data.
- Open the phone's own default app for that data when tapped — the date and
  month widgets open the default calendar app, the clock widget opens the
  default clock app — the same way tapping one of iOS's widgets hands off to
  Apple's own app instead of some other screen.

## Theme

Dark retro cards with a 2dp yellow outline. Weekday labels are teal, the
month label is white, and the day number / "today" highlight / clock minute
digits are cyan.

## Project layout

- `CalendarUtil.kt` — all the date/month math, shared by the date and month widgets.
- `DateWidgetProvider.kt` / `MonthWidgetProvider.kt` / `ClockWidgetProvider.kt` — each widget's brain.
- `DateChangeReceiver.kt` — keeps the date and month widgets in sync with the real date.
- `ClockTickReceiver.kt` — keeps the clock widget ticking once a minute.
- `WidgetLaunch.kt` — builds each widget's "open the matching default app" tap intent.
- `MainActivity.kt` — the app icon's own simple "today" screen.

## Building

```
export JAVA_HOME=/path/to/jdk17
./gradlew assembleDebug
```

Requires a `local.properties` file (not checked in) pointing `sdk.dir` at
your Android SDK.
