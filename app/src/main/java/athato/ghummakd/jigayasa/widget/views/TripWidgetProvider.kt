package athato.ghummakd.jigayasa.widget.views

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.view.View
import android.widget.RemoteViews
import athato.ghummakd.jigayasa.R
import athato.ghummakd.jigayasa.di.ServiceLocator
import athato.ghummakd.jigayasa.domain.model.Event
import athato.ghummakd.jigayasa.domain.model.SupportedCurrencies
import athato.ghummakd.jigayasa.presentation.MainActivity
import athato.ghummakd.jigayasa.presentation.util.AmountFormatter
import athato.ghummakd.jigayasa.presentation.util.EventTimeFormatter

/**
 * Home-screen widget that shows the next upcoming event with a live countdown.
 *
 * Lifecycle of an AppWidgetProvider is short — instances are recreated for each broadcast.
 * Per-second/per-minute updates therefore must be driven by the system, so we schedule
 * inexact 60-second alarms with [AlarmManager] that bounce a custom action back to this
 * provider's [onReceive].
 */
class TripWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        renderAll(context, appWidgetManager, appWidgetIds)
        scheduleTick(context)
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        scheduleTick(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        cancelTick(context)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        when (intent.action) {
            ACTION_TICK, ACTION_REFRESH -> {
                val mgr = AppWidgetManager.getInstance(context)
                val ids = mgr.getAppWidgetIds(ComponentName(context, TripWidgetProvider::class.java))
                if (ids.isNotEmpty()) {
                    renderAll(context, mgr, ids)
                    if (intent.action == ACTION_TICK) scheduleTick(context)
                }
            }
        }
    }

    private fun renderAll(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val app = context.applicationContext
        val nextGroup = ServiceLocator.nextUpcomingUseCase(app)()
        val views = buildRemoteViews(app, nextGroup)
        appWidgetManager.updateAppWidget(appWidgetIds, views)
    }

    private fun buildRemoteViews(context: Context, events: List<Event>): RemoteViews {
        val views = RemoteViews(context.packageName, R.layout.trip)
        val openApp = openAppPendingIntent(context)
        // Make the entire widget tappable.
        views.setOnClickPendingIntent(R.id.top, openApp)
        views.setOnClickPendingIntent(R.id.title, openApp)
        views.setOnClickPendingIntent(R.id.hjTextView, openApp)
        views.setOnClickPendingIntent(R.id.linear_layout_1, openApp)
        views.setOnClickPendingIntent(R.id.linear_layout_2, openApp)

        if (events.isEmpty()) {
            views.setViewVisibility(R.id.linear_layout_1, View.VISIBLE)
            views.setViewVisibility(R.id.linear_layout_2, View.GONE)
            views.setViewVisibility(R.id.title, View.GONE)
            views.setViewVisibility(R.id.hjTextView, View.VISIBLE)
            views.setTextViewText(R.id.hjTextView, "No upcoming events")
            return views
        }
        views.setViewVisibility(R.id.linear_layout_1, View.GONE)
        views.setViewVisibility(R.id.title, View.VISIBLE)
        val mergedTitle = events.joinToString(" / ") { it.title.ifBlank { "Untitled" } }
        views.setTextViewText(R.id.title, mergedTitle)
        val amountParts = events.mapNotNull { ev ->
            ev.amount?.takeIf { it > 0.0 }?.let { amt ->
                val symbol = SupportedCurrencies.find(ev.currencyCode).symbol
                "$symbol${AmountFormatter.groupIndian(amt)}"
            }
        }
        val secondary = when {
            amountParts.isNotEmpty() -> amountParts.joinToString(" / ")
            events.size == 1 -> events[0].message.takeIf { it.isNotBlank() }
            else -> events.mapNotNull { it.message.takeIf { m -> m.isNotBlank() } }
                .takeIf { it.isNotEmpty() }
                ?.joinToString(" / ")
        }
        if (secondary != null) {
            views.setViewVisibility(R.id.hjTextView, View.VISIBLE)
            views.setTextViewText(R.id.hjTextView, secondary)
        } else {
            views.setViewVisibility(R.id.hjTextView, View.GONE)
        }
        val countdown = EventTimeFormatter.countdown(events.first())
        if (countdown.isPast) {
            views.setViewVisibility(R.id.linear_layout_2, View.GONE)
        } else {
            views.setViewVisibility(R.id.linear_layout_2, View.VISIBLE)
            setUnit(views, R.id.ll_days, R.id.tv_days, countdown.totalDays)
            setUnit(views, R.id.ll_hour, R.id.tv_hour, countdown.hoursOfDay)
            setUnit(views, R.id.ll_min, R.id.tv_minute, countdown.minutesOfHour)
            setUnit(views, R.id.ll_sec, R.id.tv_second, countdown.secondsOfMinute)
        }
        return views
    }

    private fun setUnit(views: RemoteViews, containerId: Int, textId: Int, value: Long) {
        if (value > 0) {
            views.setViewVisibility(containerId, View.VISIBLE)
            views.setTextViewText(textId, "%02d".format(value))
        } else {
            views.setViewVisibility(containerId, View.GONE)
        }
    }

    private fun scheduleTick(context: Context) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pi = tickPendingIntent(context)
        am.cancel(pi)
        val triggerAt = SystemClock.elapsedRealtime() + TICK_INTERVAL_MS
        am.set(AlarmManager.ELAPSED_REALTIME, triggerAt, pi)
    }

    private fun cancelTick(context: Context) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.cancel(tickPendingIntent(context))
    }

    private fun tickPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, TripWidgetProvider::class.java).apply {
            action = ACTION_TICK
            component = ComponentName(context, TripWidgetProvider::class.java)
        }
        return PendingIntent.getBroadcast(
            context, REQUEST_TICK, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun openAppPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        return PendingIntent.getActivity(
            context, REQUEST_OPEN, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    companion object {
        const val ACTION_TICK = "athato.ghummakd.jigayasa.widget.action.TICK"
        const val ACTION_REFRESH = "athato.ghummakd.jigayasa.widget.action.REFRESH"
        private const val TICK_INTERVAL_MS = 60_000L
        private const val REQUEST_TICK = 0xA1
        private const val REQUEST_OPEN = 0xA2
    }
}
