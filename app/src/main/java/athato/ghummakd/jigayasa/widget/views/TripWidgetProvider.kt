package athato.ghummakd.jigayasa.widget.views

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.RemoteViews
import athato.ghummakd.jigayasa.R
import athato.ghummakd.jigayasa.di.ServiceLocator
import athato.ghummakd.jigayasa.domain.model.Event
import athato.ghummakd.jigayasa.presentation.MainActivity
import athato.ghummakd.jigayasa.presentation.util.EventTimeFormatter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TripWidgetProvider : AppWidgetProvider() {

    private val handler = Handler(Looper.getMainLooper())
    private var ticker: Runnable? = null

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        scheduleTick(context, appWidgetManager, appWidgetIds)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        ticker?.let { handler.removeCallbacks(it) }
        ticker = null
    }

    private fun scheduleTick(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        ticker?.let { handler.removeCallbacks(it) }
        val appContext = context.applicationContext
        ticker = object : Runnable {
            override fun run() {
                CoroutineScope(Dispatchers.Main).launch {
                    val next = withIO { ServiceLocator.nextUpcomingUseCase(appContext)() }
                    val views = buildRemoteViews(appContext, next)
                    appWidgetManager.updateAppWidget(appWidgetIds, views)
                }
                handler.postDelayed(this, 1000L)
            }
        }
        handler.post(ticker!!)
    }

    private suspend fun <T> withIO(block: suspend () -> T): T =
        kotlinx.coroutines.withContext(Dispatchers.IO) { block() }

    private fun buildRemoteViews(context: Context, event: Event?): RemoteViews {
        val views = RemoteViews(context.packageName, R.layout.trip)
        views.setOnClickPendingIntent(R.id.top, openAppPendingIntent(context))
        if (event == null) {
            views.setViewVisibility(R.id.linear_layout_1, View.VISIBLE)
            views.setViewVisibility(R.id.linear_layout_2, View.GONE)
            views.setViewVisibility(R.id.title, View.GONE)
            views.setViewVisibility(R.id.hjTextView, View.VISIBLE)
            views.setTextViewText(R.id.hjTextView, "No upcoming events")
            return views
        }
        views.setViewVisibility(R.id.linear_layout_1, View.GONE)
        views.setViewVisibility(R.id.title, View.VISIBLE)
        views.setTextViewText(R.id.title, event.title)
        if (event.message.isNotBlank()) {
            views.setViewVisibility(R.id.hjTextView, View.VISIBLE)
            views.setTextViewText(R.id.hjTextView, event.message)
        } else {
            views.setViewVisibility(R.id.hjTextView, View.GONE)
        }
        val countdown = EventTimeFormatter.countdown(event)
        if (countdown.isPast) {
            views.setViewVisibility(R.id.linear_layout_2, View.GONE)
        } else {
            views.setViewVisibility(R.id.linear_layout_2, View.VISIBLE)
            setUnit(views, R.id.ll_days, R.id.tv_days, countdown.days)
            setUnit(views, R.id.ll_hour, R.id.tv_hour, countdown.hours)
            setUnit(views, R.id.ll_min, R.id.tv_minute, countdown.minutes)
            setUnit(views, R.id.ll_sec, R.id.tv_second, countdown.seconds)
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

    private fun openAppPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
}
