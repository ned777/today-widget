# Today Widget

Two iOS-style home-screen widgets for Android, plus a minimal app screen.

## Widgets

- **"Today Clock"** — a small square glance at today: weekday + month
  (e.g. "Fri Aug") with a big day-of-month number below (e.g. "21").
- **"Today Calendar"** — a full current-month calendar grid, today
  highlighted. There's no previous/next-month navigation — like iOS's
  equivalent, it always just shows "now."

Both widgets have their own distinct label (set via `android:label` on each
receiver in the manifest), so when you go to add a widget for this app, the
OS's own widget picker already shows them as two clearly named, separately
previewed choices — "Today Clock" and "Today Calendar" — no custom in-app
picker needed.

Both widgets:
- Refresh themselves automatically at midnight (and on manual clock/timezone
  changes), so they never freeze on a stale date — see `DateChangeReceiver.kt`.
- Open the phone's own default calendar app when tapped, the same way
  tapping iOS's Calendar widgets hands off to the Calendar app.

## Theme

Dark retro cards with a 2dp yellow outline. Weekday labels are teal, the
month label is white, and the day number / "today" highlight is cyan.

## Project layout

- `CalendarUtil.kt` — all the date/month math, shared by both widgets.
- `DateWidgetProvider.kt` / `MonthWidgetProvider.kt` — each widget's brain.
- `DateChangeReceiver.kt` — keeps both widgets in sync with the real date.
- `WidgetLaunch.kt` — builds the "open the default calendar app" tap intent.
- `MainActivity.kt` — the app icon's own simple "today" screen.

## Building

```
export JAVA_HOME=/path/to/jdk17
./gradlew assembleDebug
```

Requires a `local.properties` file (not checked in) pointing `sdk.dir` at
your Android SDK.
